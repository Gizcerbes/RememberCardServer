package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.addAndOp
import com.uogames.dictinary.v3.charLength
import com.uogames.dictinary.v3.db.dao.ImageService.cleanImage
import com.uogames.dictinary.v3.db.entity.*
import com.uogames.dictinary.v3.db.entity.Card.Companion.fromEntity
import com.uogames.dictinary.v3.defaultUUID
import com.uogames.dictinary.v3.ifNull
import com.uogames.dictinary.v3.views.CardView
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object CardService {

    private fun Query.buildWhere(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        first: Alias<PhraseTable>,
        second: Alias<PhraseTable>
    ): Query {
        val firstLike by lazy { first[PhraseTable.phrase].lowerCase() like "%${text?.lowercase()}%" }
        val secondLike by lazy { second[PhraseTable.phrase].lowerCase() like "%${text?.lowercase()}%" }
        return andWhere { CardTable.ban eq false }
            .addAndOp(text) { firstLike or secondLike }
            .addAndOp(langFirst) { first[PhraseTable.lang] eq it }
            .addAndOp(langSecond) { second[PhraseTable.lang] eq it }
            .addAndOp(countryFirst) { first[PhraseTable.country] eq it }
            .addAndOp(countrySecond) { second[PhraseTable.country] eq it }
    }

    fun count(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null
    ): Long {
        val first = PhraseTable.alias("pt1")
        val second = PhraseTable.alias("pt2")

        val query = CardTable
            .join(first, JoinType.LEFT, CardTable.idPhrase, first[PhraseTable.id])
            .join(second, JoinType.LEFT, CardTable.idTranslate, second[PhraseTable.id])
            .slice(CardTable.id.countDistinct())
            .selectAll()
            .buildWhere(text, langFirst, langSecond, countryFirst, countrySecond, first, second)

        return query.first()[CardTable.id.countDistinct()]
    }

    private fun getQuery(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ): Query {
        val first = PhraseTable.alias("pt1")
        val second = PhraseTable.alias("pt2")
        val fLenAlias = first[PhraseTable.phrase].charLength().alias("len1")
        val sLenAlias = second[PhraseTable.phrase].charLength().alias("len2")
        val phraseFirst = first[PhraseTable.phrase].alias("p")
        val phraseSecond = second[PhraseTable.phrase].alias("t")

        val firstOrder = fLenAlias to SortOrder.ASC
        val firstSort = phraseFirst to SortOrder.ASC
        val secondOrder = sLenAlias to SortOrder.ASC
        val secondSort = phraseSecond to SortOrder.ASC

        val columns = ArrayList<Expression<*>>().apply {
            addAll(CardTable.columns)
            add(fLenAlias)
            add(sLenAlias)
            add(phraseFirst)
            add(phraseSecond)
        }

        return CardTable
            .join(first, JoinType.LEFT, CardTable.idPhrase, first[PhraseTable.id])
            .join(second, JoinType.LEFT, CardTable.idTranslate, second[PhraseTable.id])
            .slice(columns)
            .selectAll()
            .buildWhere(text, langFirst, langSecond, countryFirst, countrySecond, first, second)
            .orderBy(
                firstOrder,
                firstSort,
                secondOrder,
                secondSort
            )
            .limit(1, number)
            .withDistinct()
    }

    fun get(id: UUID) = CardEntity.findById(id)?.fromEntity()

    fun get(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = getQuery(
        text = text,
        langFirst = langFirst,
        langSecond = langSecond,
        countryFirst = countryFirst,
        countrySecond = countrySecond,
        number = number
    ).firstOrNull()?.let { Card.fromRow(it) }

    fun getView(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = getQuery(
        text = text,
        langFirst = langFirst,
        langSecond = langSecond,
        countryFirst = countryFirst,
        countrySecond = countrySecond,
        number = number
    ).firstOrNull()?.let { CardView.fromRow(it) }

    fun getView(eID: EntityID<UUID>) = CardEntity[eID].let { CardView.fromEntity(it) }

    fun getView(id: UUID) = CardEntity.findById(id)?.let { CardView.fromEntity(it) }


    fun new(card: Card, user: User): Card {
        UserService.update(user)
        val uuid = if (card.globalId != defaultUUID) card.globalId else null
        return CardEntity.new(uuid) {
            if (card.globalOwner.isEmpty()) card.globalOwner = user.globalOwner
            update(card)
            ban = false
        }.fromEntity()
    }

    fun update(card: Card, user: User) = transaction {
        val loaded = CardEntity.findById(card.globalId)
        return@transaction loaded?.let {
            UserService.update(user)
            val oldImage = loaded.idImage?.value
            if (loaded.globalOwner.value == user.globalOwner) loaded.update(card)
            commit()
            if (oldImage != null && oldImage != card.idImage) cleanImage(oldImage)
            loaded.fromEntity()
        }.ifNull { new(card, user) }

    }

    fun ban(
        cardId: EntityID<UUID>,
        ban: Boolean
    ) {
        CardEntity.findById(cardId)?.apply {
            this.ban = ban
        }
    }

}
package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.addAndOp
import com.uogames.dictinary.v3.charLength
import com.uogames.dictinary.v3.db.dao.ImageService.cleanImage
import com.uogames.dictinary.v3.db.dao.UserService.updateUser
import com.uogames.dictinary.v3.db.entity.*
import com.uogames.dictinary.v3.db.entity.Card.Companion.fromEntity
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
    ) = transaction {
        val first = PhraseTable.alias("pt1")
        val second = PhraseTable.alias("pt2")

        val query = CardTable
            .join(first, JoinType.LEFT, CardTable.idPhrase, first[PhraseTable.id])
            .join(second, JoinType.LEFT, CardTable.idTranslate, second[PhraseTable.id])
            .slice(CardTable.id.countDistinct())
            .selectAll()
            .buildWhere(text, langFirst, langSecond, countryFirst, countrySecond, first, second)

        return@transaction query.first()[CardTable.id.countDistinct()].toString()
    }

    fun get(id: UUID) = transaction {
        return@transaction CardEntity.findById(id)?.fromEntity()
    }

    fun get(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = transaction {
        val first = PhraseTable.alias("pt1")
        val second = PhraseTable.alias("pt2")
        val fLenAlias = first[PhraseTable.phrase].charLength().alias("len1")
        val sLenAlias = second[PhraseTable.phrase].charLength().alias("len2")

        val firstOrder = sLenAlias to SortOrder.ASC
        val secondOrder = fLenAlias to SortOrder.ASC

        val columns = ArrayList<Expression<*>>().apply {
            addAll(CardTable.columns)
            add(fLenAlias)
            add(sLenAlias)
        }

        val query = CardTable
            .join(first, JoinType.LEFT, CardTable.idPhrase, first[PhraseTable.id])
            .join(second, JoinType.LEFT, CardTable.idTranslate, second[PhraseTable.id])
            .slice(columns)
            .selectAll()
            .buildWhere(text, langFirst, langSecond, countryFirst, countrySecond, first, second)
            .orderBy(firstOrder, secondOrder)
            .limit(1, number)
            .withDistinct()

        return@transaction query.firstOrNull()?.let { Card.fromRow(it) }
    }

    fun new(card: Card, user: User) = transaction { newCard(card, user) }

    fun Transaction.newCard(card: Card, user: User): Card {
        updateUser(user)
        return CardEntity.new { update(card) }.fromEntity()
    }

    fun update(card: Card, user: User) = transaction {
        val loaded = CardEntity.findById(card.globalId)
        return@transaction if (loaded == null) {
            newCard(card, user)
        } else if (card.globalOwner == user.globalOwner) {
            updateUser(user)
            val oldImage = loaded.idImage?.value
            loaded.update(card)
            commit()
            if (oldImage != null && oldImage != card.idImage) cleanImage(oldImage)
            loaded.fromEntity()
        } else {
            null
        }
    }

}
package com.uogames.dictionary.db.entity.v2.dao

import com.uogames.dictionary.db.charLength
import com.uogames.dictionary.db.entity.v2.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object CardService {

    fun countV2(text: String) = transaction {
        val first = Phrases.alias("pt1")
        val second = Phrases.alias("pt2")
        val query = Cards
            .join(first, JoinType.LEFT, Cards.idPhrase, first[Phrases.id])
            .join(second, JoinType.LEFT, Cards.idTranslate, second[Phrases.id])
            .slice(Cards.id)
            .select((first[Phrases.phrase].lowerCase() like "%${text.lowercase()}%") or (second[Phrases.phrase].lowerCase() like "%${text.lowercase()}%"))

        return@transaction query.count()
    }

    fun get(id: UUID) = transaction {
        return@transaction CardEntity.findById(id)?.toCard()
    }


    fun get(text: String, number: Long) = transaction{
        val first = Phrases.alias("pt1")
        val second = Phrases.alias("pt2")
        val query = Cards
            .join(first, JoinType.LEFT, Cards.idPhrase, first[Phrases.id])
            .join(second, JoinType.LEFT, Cards.idTranslate, second[Phrases.id])
            .slice(Cards.columns)
            .select((first[Phrases.phrase].lowerCase() like "%${text.lowercase()}%") or (second[Phrases.phrase].lowerCase() like "%${text.lowercase()}%"))
            .orderBy(second[Phrases.phrase].charLength() to SortOrder.ASC, first[Phrases.phrase].charLength() to SortOrder.ASC)
            .limit(1, number)

        return@transaction query.firstOrNull()?.let { Card.fromRow(it) }
    }

    fun new(card: Card, user: User) = transaction {
        UserService.update(user)
        return@transaction CardEntity.new {
            update(card)
        }.toCard()
    }

    fun update(card: Card, user: User) = transaction {
        UserService.update(user)
        val loaded = CardEntity.findById(card.globalId)
        return@transaction if (loaded == null) {
            CardEntity.new(card.globalId) { update(card) }.toCard()
        } else if (card.globalOwner == user.globalOwner) {
            if (loaded.idImage?.value != card.idImage) loaded.idImage?.value?.let { ImageService.delete(it) }
            loaded.update(card)
            loaded.toCard()
        } else {
            null
        }
    }

}
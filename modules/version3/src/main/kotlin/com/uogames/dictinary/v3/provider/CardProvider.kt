package com.uogames.dictinary.v3.provider

import com.uogames.dictinary.v3.db.dao.CardService
import com.uogames.dictinary.v3.db.entity.Card
import com.uogames.dictinary.v3.db.entity.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object CardProvider {

    private val cs = CardService

    fun count(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null
    ) = transaction { cs.count(text, langFirst, langSecond, countryFirst, countrySecond) }

    fun get(id: UUID) = transaction { cs.get(id) }

    fun get(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = transaction { cs.get(text, langFirst, langSecond, countryFirst, countrySecond, number) }

    fun getView(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = transaction { cs.getView(text, langFirst, langSecond, countryFirst, countrySecond, number) }

    fun getListView(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long,
        limit: Int
    ) = transaction { cs.getListView(text, langFirst, langSecond, countryFirst, countrySecond, number, limit) }

    fun getView(id: UUID) = transaction { cs.getView(id) }

    fun new(card: Card, user: User) = transaction { cs.new(card, user) }

    fun update(card: Card, user: User) = transaction { cs.update(card, user) }

}
package com.uogames.dictinary.v3.provider

import com.uogames.dictinary.v3.db.dao.PhraseService
import com.uogames.dictinary.v3.db.entity.Phrase
import com.uogames.dictinary.v3.db.entity.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object PhraseProvider {

    private val ps = PhraseService

    fun count(
        text: String? = null,
        language: String? = null,
        country: String? = null
    ) = transaction { ps.count(text, language, country) }

    fun get(id: UUID) = transaction { ps.get(id) }

    fun get(
        text: String? = null,
        language: String? = null,
        country: String? = null,
        number: Long
    ) = transaction { ps.get(text, language, country, number) }

    fun getView(id: UUID) = transaction { ps.getView(id) }

    fun getView(
        text: String? = null,
        language: String? = null,
        country: String? = null,
        number: Long
    ) = transaction { ps.getView(text, language, country, number) }

    fun getListView(
        text: String? = null,
        language: String? = null,
        country: String? = null,
        number: Long,
        limit: Int
    ) = transaction { ps.getListView(text, language, country, number, limit) }

    fun new(phrase: Phrase, user: User) = transaction { ps.new(phrase, user) }

    fun update(
        phrase: Phrase,
        user: User
    ) = transaction { ps.update(phrase, user) }

}
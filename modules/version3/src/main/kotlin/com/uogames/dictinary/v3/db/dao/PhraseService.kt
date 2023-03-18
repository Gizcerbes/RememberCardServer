package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.addAndOp
import com.uogames.dictinary.v3.charLength
import com.uogames.dictinary.v3.db.dao.ImageService.cleanImage
import com.uogames.dictinary.v3.db.dao.PronunciationService.cleanPronunciation
import com.uogames.dictinary.v3.db.entity.Phrase
import com.uogames.dictinary.v3.db.entity.Phrase.Companion.fromEntity
import com.uogames.dictinary.v3.db.entity.PhraseEntity
import com.uogames.dictinary.v3.db.entity.PhraseTable
import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.ifNull
import com.uogames.dictinary.v3.views.PhraseView
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import java.util.UUID

object PhraseService {

    private fun Query.buildWhere(
        text: String? = null,
        language: String? = null,
        country: String? = null
    ): Query {
        return andWhere { PhraseTable.ban eq false }
            .addAndOp(text) { PhraseTable.phrase.lowerCase().like("%${it.lowercase()}%") }
            .addAndOp(language) { PhraseTable.lang eq it }
            .addAndOp(country) { PhraseTable.country eq it }
    }

    fun count(
        text: String? = null,
        language: String? = null,
        country: String? = null
    ): Long {
        val query = PhraseTable
            .slice(PhraseTable.id.countDistinct())
            .selectAll()
            .buildWhere(text, language, country)

        return query.first()[PhraseTable.id.countDistinct()]
    }


    fun get(id: UUID) = PhraseEntity.findById(id)?.fromEntity()

    private fun getQuery(
        text: String? = null,
        language: String? = null,
        country: String? = null,
        number: Long
    ): Query {
        val lenPhrase = PhraseTable.phrase.charLength().alias("len")
        val columns = ArrayList<Expression<*>>().apply {
            addAll(PhraseTable.columns)
            add(lenPhrase)
        }
        return PhraseTable
            .slice(columns)
            .selectAll()
            .buildWhere(text, language, country)
            .orderBy(
                lenPhrase to SortOrder.ASC,
                PhraseTable.phrase to SortOrder.ASC
            )
            .limit(1, number)
    }

    fun get(
        text: String? = null,
        language: String? = null,
        country: String? = null,
        number: Long
    ) = getQuery(text, language, country, number).firstOrNull()?.let { Phrase.fromRow(it) }

    fun getView(eID: EntityID<UUID>) = PhraseEntity[eID].let { PhraseView.fromEntity(it) }

    fun getView(id: UUID) = PhraseEntity.findById(id)?.let { PhraseView.fromEntity(it) }

    fun getView(
        text: String? = null,
        language: String? = null,
        country: String? = null,
        number: Long
    ) = getQuery(text, language, country, number).firstOrNull()?.let { PhraseView.fromRow(it) }

    fun new(phrase: Phrase, user: User): Phrase {
        UserService.update(user)
        return PhraseEntity.new {
            update(phrase)
            ban = false
        }.fromEntity()
    }

    fun update(
        phrase: Phrase,
        user: User
    ): Phrase {
        val loaded = PhraseEntity.findById(phrase.globalId)
        return loaded?.let {
            UserService.update(user)
            val oldImage = loaded.idImage?.value
            val oldPronounce = loaded.idPronounce?.value
            loaded.update(phrase)
            if (oldImage != null && oldImage != phrase.idImage) cleanImage(oldImage)
            if (oldPronounce != null && oldPronounce != phrase.idPronounce) cleanPronunciation(oldPronounce)
            loaded.fromEntity()
        }.ifNull {
            new(phrase, user)
        }
    }

    fun ban(
        phraseId: EntityID<UUID>,
        ban: Boolean
    ) {
        PhraseEntity[phraseId].apply { this.ban = ban }
    }

}
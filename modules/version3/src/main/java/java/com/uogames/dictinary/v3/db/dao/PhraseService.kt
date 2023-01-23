package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.addAndOp
import com.uogames.dictinary.v3.charLength
import com.uogames.dictinary.v3.db.dao.ImageService.cleanImage
import com.uogames.dictinary.v3.db.dao.PronunciationService.cleanPronunciation
import com.uogames.dictinary.v3.db.dao.UserService.updateUser
import com.uogames.dictinary.v3.db.entity.Phrase
import com.uogames.dictinary.v3.db.entity.Phrase.Companion.fromEntity
import com.uogames.dictinary.v3.db.entity.PhraseEntity
import com.uogames.dictinary.v3.db.entity.PhraseTable
import com.uogames.dictinary.v3.db.entity.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
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
    ) = transaction {
        val query = PhraseTable
            .slice(PhraseTable.id.countDistinct())
            .selectAll()
            .buildWhere(text, language, country)

        return@transaction query.first()[PhraseTable.id.countDistinct()]
    }


    fun get(id: UUID) = transaction {
        return@transaction PhraseEntity.findById(id)?.fromEntity()
    }

    fun get(
        text: String? = null,
        language: String? = null,
        country: String? = null,
        number: Long
    ) = transaction {
        val lenPhrase = PhraseTable.phrase.charLength().alias("len")
        val columns = ArrayList<Expression<*>>().apply {
            addAll(PhraseTable.columns)
            add(lenPhrase)
        }
        val query = PhraseTable
            .slice(columns)
            .selectAll()
            .buildWhere(text, language, country)
            .orderBy(lenPhrase)
            .limit(1, number)

        return@transaction query.firstOrNull()?.let { Phrase.fromRow(it) }
    }

    fun new(phrase: Phrase, user: User) = transaction { newPhrase(phrase, user) }

    fun Transaction.newPhrase(phrase: Phrase, user: User): Phrase {
        updateUser(user)
        return PhraseEntity.new { update(phrase) }.fromEntity()
    }

    fun update(
        phrase: Phrase,
        user: User
    ) = transaction {
        val loaded = PhraseEntity.findById(phrase.globalId)
        val res = if (loaded == null) {
            newPhrase(phrase, user)
        } else if (loaded.globalOwner.value == user.globalOwner) {
            updateUser(user)
            val oldImage = loaded.idImage?.value
            val oldPronounce = loaded.idPronounce?.value
            loaded.update(phrase)
            commit()
            if (oldImage != null && oldImage != phrase.idImage) cleanImage(oldImage)
            if (oldPronounce != null && oldPronounce != phrase.idPronounce) cleanPronunciation(oldPronounce)
            loaded.fromEntity()
        } else {
            null
        }
        return@transaction res
    }


}
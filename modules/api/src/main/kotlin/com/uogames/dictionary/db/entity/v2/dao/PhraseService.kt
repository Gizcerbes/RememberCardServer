package com.uogames.dictionary.db.entity.v2.dao

import com.uogames.dictionary.db.charLength
import com.uogames.dictionary.db.entity.v2.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object PhraseService {

    fun count(like: String) = transaction {
        return@transaction PhraseEntity.count(Phrases.phrase.lowerCase().like("%${like.lowercase()}%"))
    }

    fun get(id: UUID) = transaction {
        return@transaction PhraseEntity.findById(id)?.toPhrase()
    }

    fun get(like: String, number: Long) = transaction {
        return@transaction PhraseEntity
            .find(Phrases.phrase.lowerCase().like("%${like.lowercase()}%"))
            .orderBy(Phrases.phrase.charLength() to SortOrder.ASC)
            .limit(1, number)
            .firstOrNull()?.toPhrase()
    }

    fun new(phrase: Phrase, user: User) = transaction {
        UserService.update(user)
        return@transaction PhraseEntity.new { update(phrase) }.toPhrase()
    }

    fun update(phrase: Phrase, user: User) = transaction {
        UserService.update(user)
        val loaded = PhraseEntity.findById(phrase.globalId)
        return@transaction if (loaded == null) {
            PhraseEntity.new(phrase.globalId) { update(phrase) }.toPhrase()
        } else if (loaded.globalOwner.value == user.globalOwner) {
            if (loaded.idImage?.value != phrase.idImage) loaded.idImage?.value?.let { ImageService.delete(it) }
            if (loaded.idPronounce?.value != phrase.idPronounce) loaded.idPronounce?.value?.let {
                PronunciationService.delete(
                    it
                )
            }
            loaded.update(phrase)
            loaded.toPhrase()
        } else {
            null
        }
    }


}
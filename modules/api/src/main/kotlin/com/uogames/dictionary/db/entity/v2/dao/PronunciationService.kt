package com.uogames.dictionary.db.entity.v2.dao

import com.uogames.dictionary.db.entity.v2.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object PronunciationService {

    fun get(pronunciationID: UUID) = transaction {
        return@transaction PronunciationEntity.findById(pronunciationID)?.toPronunciation()
    }

    fun new(pronunciation: Pronunciation, user: User) = transaction {
        UserService.update(user)
        return@transaction PronunciationEntity.new {
            update(pronunciation)
        }.toPronunciation()
    }

    fun update(pronunciation: Pronunciation, user: User) = transaction {
        UserService.update(user)
        return@transaction PronunciationEntity.findById(pronunciation.globalId)?.apply {
            update(pronunciation)
        }?.toPronunciation()
    }

    fun delete(pronunciationID: UUID) = transaction {
        PronunciationEntity.findById(pronunciationID)?.delete()
    }


}
package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.db.entity.*
import com.uogames.dictinary.v3.db.entity.Pronunciation.Companion.fromEntity
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object PronunciationService {

    fun get(pronunciationID: UUID) = transaction {
        return@transaction PronunciationEntity.findById(pronunciationID)?.fromEntity()
    }

    fun new(pronunciation: Pronunciation, user: User) = transaction {
        UserService.update(user)
        return@transaction PronunciationEntity.new {
            update(pronunciation)
        }.fromEntity()
    }

    fun update(pronunciation: Pronunciation, user: User) = transaction {
        UserService.update(user)
        return@transaction PronunciationEntity.findById(pronunciation.globalId)?.apply {
            update(pronunciation)
        }?.fromEntity()
    }

    fun delete(pronunciationID: UUID) = transaction {
        this.deletePronunciation(pronunciationID)
    }

    fun Transaction.deletePronunciation(pronunciationID: UUID) {
        PronunciationEntity.findById(pronunciationID)?.delete()
    }

    fun Transaction.cleanPronunciation(id: UUID) {
        val count = PronunciationTable
            .join(PhraseTable,JoinType.LEFT,PronunciationTable.id, PhraseTable.idPronounce)
            .select(PhraseTable.idPronounce eq id)
            .count()
        if (count == 0L) this.deletePronunciation(id)
    }


}
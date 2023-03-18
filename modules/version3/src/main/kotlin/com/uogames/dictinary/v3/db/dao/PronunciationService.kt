package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.db.entity.*
import com.uogames.dictinary.v3.db.entity.Pronunciation.Companion.fromEntity
import com.uogames.dictinary.v3.views.PronunciationView
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.countDistinct
import org.jetbrains.exposed.sql.select
import java.util.UUID

object PronunciationService {

    fun get(pronunciationID: UUID) = PronunciationEntity.findById(pronunciationID)?.fromEntity()

    fun getView(eID: EntityID<UUID>) = PronunciationEntity[eID].let { PronunciationView.fromEntity(it) }

    fun getView(pronunciationID: UUID) = PronunciationEntity
        .findById(pronunciationID)?.let { PronunciationView.fromEntity(it) }

    fun new(pronunciation: Pronunciation, user: User):Pronunciation {
        UserService.update(user)
        return PronunciationEntity.new {
            update(pronunciation)
            ban = false
        }.fromEntity()
    }

    fun update(pronunciation: Pronunciation, user: User): Pronunciation? {
        UserService.update(user)
        return PronunciationEntity.findById(pronunciation.globalId)?.apply {
            update(pronunciation)
        }?.fromEntity()
    }

    fun delete(pronunciationID: UUID) {
        PronunciationEntity.findById(pronunciationID)?.delete()
    }

    fun cleanPronunciation(id: UUID) {
        val count = PronunciationTable
            .join(PhraseTable,JoinType.LEFT,PronunciationTable.id, PhraseTable.idPronounce)
            .slice(PronunciationTable.id.countDistinct())
            .select(PhraseTable.idPronounce eq id)
            .first()[PronunciationTable.id.countDistinct()]
        if (count == 0L) delete(id)
    }


}
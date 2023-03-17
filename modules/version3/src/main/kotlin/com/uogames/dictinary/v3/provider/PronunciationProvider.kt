package com.uogames.dictinary.v3.provider

import com.uogames.dictinary.v3.db.dao.PronunciationService
import com.uogames.dictinary.v3.db.dao.PronunciationService.cleanPronunciation
import com.uogames.dictinary.v3.db.entity.Pronunciation
import com.uogames.dictinary.v3.db.entity.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object PronunciationProvider {

    private val ps = PronunciationService

    fun get(pronunciationID: UUID) = transaction { ps.get(pronunciationID) }

    fun getView(pronunciationID: UUID) = transaction { ps.getView(pronunciationID) }

    fun new(pronunciation: Pronunciation, user: User) = transaction { ps.new(pronunciation, user) }

    fun update(pronunciation: Pronunciation, user: User) = transaction { ps.update(pronunciation, user) }

    fun delete(pronunciationID: UUID) = transaction { ps.delete(pronunciationID) }

    fun cleanPronunciation(id: UUID) = transaction { ps.cleanPronunciation(id) }

}
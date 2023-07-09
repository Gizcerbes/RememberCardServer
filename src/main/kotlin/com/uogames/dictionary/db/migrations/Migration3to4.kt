package com.uogames.dictionary.db.migrations

import com.uogames.dictinary.v3.db.entity.PhraseEntity
import com.uogames.dictionary.db.ConfigEntity
import org.jetbrains.exposed.sql.transactions.transaction

object Migration3to4 {


    fun migration() = transaction {
        exec("ALTER TABLE phrase_table_v3 ADD COLUMN length SMALLINT DEFAULT 0")
        PhraseEntity.all().forEach {
            it.length = it.phrase.length.toShort()
        }
        ConfigEntity.findById("version")?.let { it.value = "4" }
    }

}
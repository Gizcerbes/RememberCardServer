package com.uogames.dictionary.db.migrations

import com.uogames.dictionary.db.ConfigEntity
import org.jetbrains.exposed.sql.transactions.transaction

object Migration4to5 {

    fun migration() = transaction {
        exec("ALTER TABLE card_table_v3 RENAME TO card_table")
        exec("ALTER TABLE image_table_v3 RENAME TO image_table")
        exec("ALTER TABLE module_card_table_v3 RENAME TO module_card_table")
        exec("ALTER TABLE module_table_v3 RENAME TO module_table")
        exec("ALTER TABLE phrase_table_v3 RENAME TO phrase_table")
        exec("ALTER TABLE pronunciation_table_v3 RENAME TO pronunciation_table")
        exec("ALTER TABLE report_table_v3 RENAME TO report_table")
        exec("ALTER TABLE user_table_v3 RENAME TO user_table")

        ConfigEntity.findById("version")?.let { it.value = "5" }
    }

}
package com.uogames.dictionary.db.migrations

import com.uogames.dictionary.db.ConfigEntity
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Migration3to4 {

    object Old : UUIDTable(name = "phrase_table_v3", columnName = "global_id") {
        val phrase = text("phrase")
        val length = short("length").default(0).index()
    }

    fun migration() = transaction {
        exec("ALTER TABLE phrase_table_v3 ADD COLUMN length SMALLINT DEFAULT 0")

        Old.selectAll().forEach { res ->
            Old.update({ Old.id eq res[Old.id] }) {
                it[length] = res[phrase].length.toShort()
            }
        }

        ConfigEntity.findById("version")?.let { it.value = "4" }
    }

}
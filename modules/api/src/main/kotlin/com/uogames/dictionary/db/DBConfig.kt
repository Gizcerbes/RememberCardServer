package com.uogames.dictionary.db

import com.uogames.dictinary.v3.db.entity.ReportTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionManager

const val HIKARI_CONFIG_KEY = "ktor.hikariConfig"

fun Application.initDB() {
    val configPath = environment.config.property(HIKARI_CONFIG_KEY).getString()
    val dbConfig = HikariConfig(configPath)
    val dataSource = HikariDataSource(dbConfig)
    val database = Database.connect(dataSource)
    create()
}

private fun create() = transaction {
    SchemaUtils.create(Config)
    // migration to v3
    while(true) when(ConfigEntity.findById("version")?.value?.toInt() ?: 1){
        1 -> Migration1to2().migration1to2()
        2 -> Migration2to3().migration2to3()
        in 3..Int.MAX_VALUE -> break
        else -> Migration2to3().createDBv3()
    }
//    val res = db.connector().prepareStatement(
//        "SELECT
//        phrase_table_v3.global_id,
//        phrase_table_v3.global_owner,
//        phrase_table_v3.phrase,
//        phrase_table_v3.definition,
//        phrase_table_v3.lang,
//        phrase_table_v3.country,
//        phrase_table_v3.pronounce_id,
//        phrase_table_v3.image_id,
//        phrase_table_v3.time_change,
//        phrase_table_v3.\"like\",
//        phrase_table_v3.dislike,
//        phrase_table_v3.ban,
//        CHAR_LENGTH(phrase_table_v3.phrase) len
//        FROM phrase_table_v3
//        WHERE
//        phrase_table_v3.ban = FALSE ORDER BY len ASC",
//        false
//    ).executeQuery()
//    while(res.next()){
//        println(res.getString(3))
//    }

}

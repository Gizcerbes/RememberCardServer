package com.uogames.dictionary.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

const val HIKARI_CONFIG_KEY = "ktor.hikariConfig"

fun Application.initDB() {
    val configPath = environment.config.property(HIKARI_CONFIG_KEY).getString()
    val dbConfig = HikariConfig(configPath)
    val dataSource = HikariDataSource(dbConfig)
    Database.connect(dataSource)
    rename()
    create()
}

private fun rename() = transaction {
    //db.connector().prepareStatement("alter table user_table_v4 rename to user_table_v3", false).executeQuery()
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

}

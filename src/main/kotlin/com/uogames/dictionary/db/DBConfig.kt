package com.uogames.dictionary.db

import com.uogames.dictinary.v3.db.entity.PhraseTable
import com.uogames.dictionary.db.migrations.Migration2to3
import com.uogames.dictionary.db.migrations.Migration3to4
import com.uogames.dictionary.db.migrations.Migration4to5
import com.uogames.test.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

//const val HIKARI_CONFIG_KEY = "ktor.hikariConfig"
//const val HIKARI_CONFIG_KEY = "app/dbconfig.properties"

fun initDB(configPath: String) {
    val dbConfig = HikariConfig(configPath)
    prepare(dbConfig)
    val dataSource = HikariDataSource(dbConfig)
    Database.connect(dataSource)
    create()
    //createTest()
}

private fun prepare(config: HikariConfig) {
    val database = Database.connect(
        url = config.dataSourceProperties.getProperty("rootUrl"),
        driver = config.driverClassName,
        user = config.dataSourceProperties.getProperty("user"),
        password = config.dataSourceProperties.getProperty("password"),
    )
    val databaseName = config.dataSourceProperties.getProperty("databaseName")
    transaction(database) {
        val datNameObj = object : Table("pg_database") {
            val datName = text("datname")
        }
        if (datNameObj.select(datNameObj.datName eq databaseName).count() == 0L) {
            connection.autoCommit = true
            SchemaUtils.createDatabase(databaseName)
            connection.autoCommit = false
        }
    }
    TransactionManager.closeAndUnregister(database)
}

private fun create() = transaction {
    //SchemaUtils.create(PhraseTable)
    SchemaUtils.create(Config)
    // migration to v3
    while (true) when (ConfigEntity.findById("version")?.value?.toInt() ?: 1) {
        2 -> Migration2to3().migration2to3()
        3 -> Migration3to4.migration()
        4 -> Migration4to5.migration()
        in 5..Int.MAX_VALUE -> break
        else -> Migration4to5.createDBv5()
    }

}

private fun createTest() = transaction {
    SchemaUtils.create(
        Users,
        RefreshTokens
    )
}

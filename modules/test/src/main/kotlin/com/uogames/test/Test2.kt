package com.uogames.test

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime

object Users : UUIDTable(name = "user_test", columnName = "user_id") {
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 60)
}

object RefreshTokens: UUIDTable(name = "token_test", columnName = "refresh_token_id") {
    val userId = reference("user_id", Users).uniqueIndex()
    val token = varchar("token", 255)
    val expiresAt = datetime("expires_at")
}

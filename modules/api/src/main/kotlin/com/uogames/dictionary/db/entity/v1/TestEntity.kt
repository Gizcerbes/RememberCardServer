package com.uogames.dictionary.db.entity.v1

import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

object UserTable : IdTable<UUID>("users") {
    override val id = uuid("id").autoGenerate().entityId()
    override val primaryKey: PrimaryKey = PrimaryKey(id)
    val name = varchar("name", 512).nullable()
}

class TestEntity {
}
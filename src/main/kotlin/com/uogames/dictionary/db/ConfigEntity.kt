package com.uogames.dictionary.db

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object Config : IdTable<String>(name = "configs") {
    override val id = varchar("name", 100).entityId()
    override val primaryKey = PrimaryKey(id)
    val value = varchar("value", 100)
}


class ConfigEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, ConfigEntity>(Config)

    var value by Config.value

}
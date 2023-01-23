package com.uogames.dictionary.db.entity.v2

import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object Users : IdTable<String>("users_v2") {
    override val id: Column<EntityID<String>> = varchar("global_owner", 100).entityId()
    override val primaryKey: PrimaryKey = PrimaryKey(id)
    val name = varchar("name", 100)

}


class UserEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, UserEntity>(Users)

    var name by Users.name

    fun toUser() = User(
        globalOwner = id.value,
        name = name
    )

}


data class User(
    @SerializedName("global_owner")
    val globalOwner: String,
    @SerializedName("name")
    val name: String = ""
)
package com.uogames.dictinary.v3.db.entity

import com.google.gson.annotations.SerializedName
import com.uogames.dictinary.v3.views.UserView
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Transaction

object UserTable : IdTable<String>("user_table") {
    override val id: Column<EntityID<String>> = varchar("global_owner", 100).entityId()
    override val primaryKey: PrimaryKey = PrimaryKey(id)
    val name = varchar("name", 100)
    val strike = integer("strike")
    val ban = bool("ban")
}


class UserEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, UserEntity>(UserTable)

    var name by UserTable.name
    var strike by UserTable.strike
    var ban by UserTable.ban

    fun toUser() = User(
        globalOwner = id.value,
        name = name
    )

}

data class User(
    @SerializedName("global_owner")
    val globalOwner: String,
    @SerializedName("name")
    val name: String = "",
    @Transient
    val strike: Int = 0,
    @Transient
    val ban: Boolean = false
) {

    companion object : TableMapper<UserEntity, User> {
        override fun fromRow(row: ResultRow) = User(
            globalOwner = row[UserTable.id].value,
            name = row[UserTable.name],
            strike = row[UserTable.strike],
            ban = row[UserTable.ban]
        )

        override fun UserEntity.fromEntity() = User(
            globalOwner = id.value,
            name = name,
            strike = strike,
            ban = ban
        )

    }

}



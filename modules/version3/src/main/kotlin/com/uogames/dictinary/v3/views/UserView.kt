package com.uogames.dictinary.v3.views

import com.google.gson.annotations.SerializedName
import com.uogames.dictinary.v3.db.entity.UserEntity
import com.uogames.dictinary.v3.db.entity.UserTable
import com.uogames.dictinary.v3.db.entity.ViewMapper
import org.jetbrains.exposed.sql.ResultRow

data class UserView(
    @SerializedName("global_owner")
    val globalOwner: String,
    @SerializedName("name")
    val name: String = ""
){
    companion object : ViewMapper<UserEntity, UserView> {
        override fun fromRow(row: ResultRow) = UserView(
            globalOwner = row[UserTable.id].value,
            name = row[UserTable.name]
        )


        override fun fromEntity(entity: UserEntity) = UserView(
            globalOwner = entity.id.value,
            name = entity.name
        )

    }
}
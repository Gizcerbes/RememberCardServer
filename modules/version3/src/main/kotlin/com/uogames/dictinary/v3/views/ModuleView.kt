package com.uogames.dictinary.v3.views

import com.google.gson.annotations.SerializedName
import com.uogames.dictinary.v3.db.dao.UserService
import com.uogames.dictinary.v3.db.entity.ModuleEntity
import com.uogames.dictinary.v3.db.entity.ModuleTable
import com.uogames.dictinary.v3.db.entity.ViewMapper
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

data class ModuleView(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("user")
    var user: UserView,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("time_change")
    var timeChange: Long = 0,
    @SerializedName("like")
    var like: Long = 0,
    @SerializedName("dislike")
    var dislike: Long = 0
){

    companion object : ViewMapper<ModuleEntity, ModuleView> {
        override fun fromRow(row: ResultRow) = ModuleView(
            globalId = row[ModuleTable.id].value,
            user = row[ModuleTable.globalOwner].let { UserService.getView(it) },
            name = row[ModuleTable.name],
            timeChange = row[ModuleTable.timeChange],
            like = row[ModuleTable.like],
            dislike = row[ModuleTable.dislike]
        )

        override fun fromEntity(entity: ModuleEntity) = ModuleView(
            name = entity.name,
            timeChange = entity.timeChange,
            like = entity.like,
            dislike = entity.dislike,
            globalId = entity.id.value,
            user = entity.globalOwner.let { UserService.getView(it) }
        )
    }

}
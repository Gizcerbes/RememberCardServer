package com.uogames.dictinary.v3.db.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

object ModuleTable : UUIDTable("module_table_v3", "global_id") {
    val globalOwner = reference("global_owner", UserTable.id, ReferenceOption.CASCADE)
    val name = text("name")
    val timeChange = long("time_change")
    val like = long("like")
    val dislike = long("dislike")
    val ban = bool("ban")
}

class ModuleEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ModuleEntity>(ModuleTable)

    var name by ModuleTable.name
    var timeChange by ModuleTable.timeChange
    var like by ModuleTable.like
    var dislike by ModuleTable.dislike
    var globalOwner by ModuleTable.globalOwner
    var ban by ModuleTable.ban

    fun update(module: Module) {
        name = module.name
        timeChange = module.timeChange
        like = module.like
        dislike = module.dislike
        globalOwner = UserEntity[module.globalOwner].id
    }

}

data class Module(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("global_owner")
    var globalOwner: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("time_change")
    var timeChange: Long = 0,
    @SerializedName("like")
    var like: Long = 0,
    @SerializedName("dislike")
    var dislike: Long = 0,
    @Transient
    var ban: Boolean = false
) {

    companion object: TableMapper<ModuleEntity, Module>{
        override fun fromRow(row: ResultRow) = Module(
            globalId = row[ModuleTable.id].value,
            globalOwner = row[ModuleTable.globalOwner].value,
            name = row[ModuleTable.name],
            timeChange = row[ModuleTable.timeChange],
            like = row[ModuleTable.like],
            dislike = row[ModuleTable.dislike],
            ban = row[ModuleTable.ban]
        )

        override fun ModuleEntity.fromEntity() = Module(
            name = name,
            timeChange = timeChange,
            like = like,
            dislike = dislike,
            globalId = id.value,
            globalOwner = globalOwner.value,
            ban = ban
        )

    }


}


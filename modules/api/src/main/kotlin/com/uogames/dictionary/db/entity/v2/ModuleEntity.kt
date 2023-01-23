package com.uogames.dictionary.db.entity.v2

import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

object Modules : UUIDTable("modules_v2", "global_id") {
    val globalOwner = reference("global_owner", Users.id, ReferenceOption.CASCADE)
    val name = text("name")
    val timeChange = long("time_change")
    val like = long("like")
    val dislike = long("dislike")
}

class ModuleEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ModuleEntity>(Modules)

    var name by Modules.name
    var timeChange by Modules.timeChange
    var like by Modules.like
    var dislike by Modules.dislike
    var globalOwner by Modules.globalOwner

    fun toModule() = Module(
        name = name,
        timeChange = timeChange,
        like = like,
        dislike = dislike,
        globalId = id.value,
        globalOwner = globalOwner.value
    )

    fun update(module: Module) {
        name = module.name
        timeChange = module.timeChange
        like = module.like
        dislike = module.dislike
        globalOwner = UserEntity[module.globalOwner].id
    }

}

data class Module(
    val globalId: UUID,
    var globalOwner: String = "",
    var name: String = "",
    var timeChange: Long = 0,
    var like: Long = 0,
    var dislike: Long = 0
)
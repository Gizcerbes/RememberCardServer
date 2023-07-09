package com.uogames.dictinary.v3.db.entity

import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

object ModuleCardTable : UUIDTable("module_card_table", "global_id") {
    val globalOwner = reference("global_owner", UserTable.id, ReferenceOption.CASCADE)
    val moduleId = reference("module_id", ModuleTable.id, ReferenceOption.CASCADE)
    val cardId = reference("card_id", CardTable.id, ReferenceOption.CASCADE)
}

class ModuleCardEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ModuleCardEntity>(ModuleCardTable)

    var moduleId by ModuleCardTable.moduleId
    var cardId by ModuleCardTable.cardId
    var globalOwner by ModuleCardTable.globalOwner

    fun update(moduleCard: ModuleCard) {
        moduleId = ModuleEntity[moduleCard.idModule].id
        cardId = CardEntity[moduleCard.idCard].id
        globalOwner = UserEntity[moduleCard.globalOwner].id
    }

}

data class ModuleCard(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("global_owner")
    var globalOwner: String = "",
    @SerializedName("module_id")
    var idModule: UUID,
    @SerializedName("card_id")
    var idCard: UUID
) {

    companion object :TableMapper<ModuleCardEntity, ModuleCard>{
        override fun fromRow(row: ResultRow) = ModuleCard(
            globalId = row[ModuleCardTable.id].value,
            globalOwner = row[ModuleCardTable.globalOwner].value,
            idModule = row[ModuleCardTable.moduleId].value,
            idCard = row[ModuleCardTable.cardId].value
        )

        override fun ModuleCardEntity.fromEntity() = ModuleCard(
            idModule = moduleId.value,
            idCard = cardId.value,
            globalId = id.value,
            globalOwner = globalOwner.value
        )

    }

}
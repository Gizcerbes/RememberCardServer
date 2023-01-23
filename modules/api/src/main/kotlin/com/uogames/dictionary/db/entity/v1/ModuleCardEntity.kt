package com.uogames.dictionary.db.entity.v1

import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

object ModuleCards : UUIDTable("module_cards", "global_id") {
    val globalOwner = reference("global_owner", Users.id, ReferenceOption.CASCADE)
    val moduleId = reference("module_id", Modules.id, ReferenceOption.CASCADE)
    val cardId = reference("card_id", Cards.id, ReferenceOption.CASCADE)
}

class ModuleCardEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ModuleCardEntity>(ModuleCards)

    var moduleId by ModuleCards.moduleId
    var cardId by ModuleCards.cardId
    var globalOwner by ModuleCards.globalOwner

    fun toModuleCard() = ModuleCard(
        idModule = moduleId.value,
        idCard = cardId.value,
        globalId = id.value,
        globalOwner = globalOwner.value
    )

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
)
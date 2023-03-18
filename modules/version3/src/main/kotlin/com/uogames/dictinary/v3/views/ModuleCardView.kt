package com.uogames.dictinary.v3.views

import com.google.gson.annotations.SerializedName
import com.uogames.dictinary.v3.db.dao.CardService
import com.uogames.dictinary.v3.db.dao.ModuleService
import com.uogames.dictinary.v3.db.dao.UserService
import com.uogames.dictinary.v3.db.entity.ModuleCardEntity
import com.uogames.dictinary.v3.db.entity.ModuleCardTable
import com.uogames.dictinary.v3.db.entity.ViewMapper
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

data class ModuleCardView(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("user")
    var user: UserView,
    @SerializedName("module")
    var idModule: ModuleView,
    @SerializedName("card")
    var card: CardView
) {

    companion object : ViewMapper<ModuleCardEntity, ModuleCardView> {
        override fun fromRow(row: ResultRow) = ModuleCardView(
            globalId = row[ModuleCardTable.id].value,
            user = row[ModuleCardTable.globalOwner].let { UserService.getView(it) },
            idModule = row[ModuleCardTable.moduleId].let { ModuleService.getView(it) },
            card = row[ModuleCardTable.cardId].let { CardService.getView(it) }
        )

        override fun fromEntity(entity: ModuleCardEntity) = ModuleCardView(
            idModule = entity.moduleId.let { ModuleService.getView(it) },
            card = entity.cardId.let { CardService.getView(it) },
            globalId = entity.id.value,
            user = entity.globalOwner.let { UserService.getView(it) }
        )
    }

}
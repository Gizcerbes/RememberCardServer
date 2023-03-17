package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.db.entity.ModuleCard
import com.uogames.dictinary.v3.db.entity.ModuleCard.Companion.fromEntity
import com.uogames.dictinary.v3.db.entity.ModuleCardEntity
import com.uogames.dictinary.v3.db.entity.ModuleCardTable
import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.views.ModuleCardView
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.UUID

object ModuleCardService {

    fun count(moduleID: UUID) = ModuleCardEntity.count(ModuleCardTable.moduleId.eq(moduleID))

    fun get(id: UUID) = ModuleCardEntity.findById(id)?.fromEntity()

    fun get(moduleID: UUID, number: Long) = ModuleCardEntity
        .find(ModuleCardTable.moduleId.eq(moduleID))
        .limit(1, number)
        .firstOrNull()?.fromEntity()

    fun getView(id:UUID) = ModuleCardEntity.findById(id)?.let { ModuleCardView.fromEntity(it) }

    fun getView(eID: EntityID<UUID>) = ModuleCardEntity[eID].let { ModuleCardView.fromEntity(it) }

    fun getView(id: UUID, number: Long) = ModuleCardEntity
        .find(ModuleCardTable.moduleId.eq(id))
        .limit(1, number)
        .firstOrNull()?.let { ModuleCardView.fromEntity(it) }

    fun new(moduleCard: ModuleCard, user: User): ModuleCard {
        UserService.update(user)
        return ModuleCardEntity.new {
            update(moduleCard)
        }.fromEntity()
    }


    fun update(moduleCard: ModuleCard, user: User):ModuleCard? {
        UserService.update(user)
        val loaded = ModuleCardEntity.findById(moduleCard.globalId)
        return if (loaded == null) {
            ModuleCardEntity.new(moduleCard.globalId) { update(moduleCard) }.fromEntity()
        } else if (loaded.globalOwner.value == user.globalOwner) {
            loaded.update(moduleCard)
            loaded.fromEntity()
        } else {
            null
        }
    }


}
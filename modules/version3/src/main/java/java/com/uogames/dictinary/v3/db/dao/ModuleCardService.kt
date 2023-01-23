package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.db.dao.UserService.updateUser
import com.uogames.dictinary.v3.db.entity.ModuleCard
import com.uogames.dictinary.v3.db.entity.ModuleCard.Companion.fromEntity
import com.uogames.dictinary.v3.db.entity.ModuleCardEntity
import com.uogames.dictinary.v3.db.entity.ModuleCardTable
import com.uogames.dictinary.v3.db.entity.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object ModuleCardService {

    fun count(moduleID: UUID) = transaction {
        return@transaction ModuleCardEntity.count(ModuleCardTable.moduleId.eq(moduleID))
    }

    fun get(id: UUID) = transaction {
        return@transaction ModuleCardEntity.findById(id)?.fromEntity()
    }

    fun get(moduleID: UUID, number: Long) = transaction {
        return@transaction ModuleCardEntity
            .find(ModuleCardTable.moduleId.eq(moduleID))
            .limit(1, number)
            .firstOrNull()?.fromEntity()
    }

    fun new(moduleCard: ModuleCard, user: User) = transaction {
        UserService.update(user)
        return@transaction ModuleCardEntity.new {
            update(moduleCard)
        }.fromEntity()
    }


    fun update(moduleCard: ModuleCard, user: User) = transaction {
        updateUser(user)
        val loaded = ModuleCardEntity.findById(moduleCard.globalId)
        return@transaction if (loaded == null) {
            ModuleCardEntity.new(moduleCard.globalId) { update(moduleCard) }.fromEntity()
        } else if (loaded.globalOwner.value == user.globalOwner) {
            loaded.update(moduleCard)
            loaded.fromEntity()
        } else {
            null
        }
    }



}
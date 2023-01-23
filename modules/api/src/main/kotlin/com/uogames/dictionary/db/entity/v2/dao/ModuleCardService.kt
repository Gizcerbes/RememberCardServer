package com.uogames.dictionary.db.entity.v2.dao

import com.uogames.dictionary.db.entity.v2.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object ModuleCardService {

    fun count(moduleID: UUID) = transaction {
        return@transaction ModuleCardEntity.count(ModuleCards.moduleId.eq(moduleID))
    }

    fun get(id: UUID) = transaction {
        return@transaction ModuleCardEntity.findById(id)?.toModuleCard()
    }

    fun get(moduleID: UUID, number: Long) = transaction {
        return@transaction ModuleCardEntity
            .find(ModuleCards.moduleId.eq(moduleID))
            .limit(1, number)
            .firstOrNull()?.toModuleCard()
    }

    fun new(moduleCard: ModuleCard, user: User) = transaction {
        UserService.update(user)
        return@transaction ModuleCardEntity.new {
            update(moduleCard)
        }.toModuleCard()
    }


    fun update(moduleCard: ModuleCard, user: User) = transaction {
        UserService.update(user)
        val loaded = ModuleCardEntity.findById(moduleCard.globalId)
        return@transaction if (loaded == null) {
            ModuleCardEntity.new(moduleCard.globalId) { update(moduleCard) }.toModuleCard()
        } else if (loaded.globalOwner.value == user.globalOwner) {
            loaded.update(moduleCard)
            loaded.toModuleCard()
        } else {
            null
        }
    }



}
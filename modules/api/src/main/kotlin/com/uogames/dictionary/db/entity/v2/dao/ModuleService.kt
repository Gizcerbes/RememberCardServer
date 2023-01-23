package com.uogames.dictionary.db.entity.v2.dao

import com.uogames.dictionary.db.entity.v2.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object ModuleService {

    fun count(like: String) = transaction {
        return@transaction ModuleEntity.count(Modules.name.lowerCase().like("%${like.lowercase()}%"))
    }

    fun get(id: UUID) = transaction {
        return@transaction ModuleEntity.findById(id)?.toModule()
    }

    fun get(like: String, number: Long) = transaction {
        return@transaction ModuleEntity
            .find(Modules.name.lowerCase().like("%${like.lowercase()}%"))
            .limit(1, number)
            .firstOrNull()?.toModule()
    }

    fun new(module: Module, user: User) = transaction {
        UserService.update(user)
        return@transaction ModuleEntity.new {
            update(module)
        }.toModule()
    }

    fun update(module: Module, user: User) = transaction {
        UserService.update(user)
        val loaded = ModuleEntity.findById(module.globalId)
        return@transaction if (loaded == null) {
            ModuleEntity.new(module.globalId) { update(module) }.toModule()
        } else if (loaded.globalOwner.value == user.globalOwner) {
            ModuleCards.deleteWhere { ModuleCards.moduleId eq module.globalId }
            loaded.update(module)
            loaded.toModule()
        } else {
            null
        }
    }

}
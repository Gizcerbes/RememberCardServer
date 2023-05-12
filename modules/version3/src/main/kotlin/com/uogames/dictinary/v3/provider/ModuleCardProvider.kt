package com.uogames.dictinary.v3.provider

import com.uogames.dictinary.v3.db.dao.ModuleCardService
import com.uogames.dictinary.v3.db.entity.ModuleCard
import com.uogames.dictinary.v3.db.entity.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object ModuleCardProvider {

    private val mcs = ModuleCardService

    fun count(id: UUID) = transaction { mcs.count(id) }

    fun get(id: UUID) = transaction { mcs.get(id) }

    fun get(moduleID: UUID, number: Long) = transaction { mcs.get(moduleID, number) }

    fun getView(id: UUID) = transaction { mcs.getView(id) }

    fun getView(moduleID: UUID, number: Long) = transaction { mcs.getView(moduleID, number) }

    fun new(moduleCard: ModuleCard, user: User) = transaction { mcs.new(moduleCard, user) }

    fun update(moduleCard: ModuleCard, user: User) = transaction {
        val  r = mcs.update(moduleCard, user)
        commit()
        return@transaction r
    }

}
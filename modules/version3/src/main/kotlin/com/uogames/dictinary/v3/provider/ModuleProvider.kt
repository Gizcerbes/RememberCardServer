package com.uogames.dictinary.v3.provider

import com.uogames.dictinary.v3.db.dao.ModuleService
import com.uogames.dictinary.v3.db.entity.Module
import com.uogames.dictinary.v3.db.entity.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object ModuleProvider {

    private val ms = ModuleService

    fun count(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null
    ) = transaction { ms.count(text, langFirst, langSecond, countryFirst, countrySecond) }

    fun get(id: UUID) = transaction { ms.get(id) }

    fun get(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = transaction { ms.get(text, langFirst, langSecond, countryFirst, countrySecond, number) }

    fun getView(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = transaction { ms.getView(text, langFirst, langSecond, countryFirst, countrySecond, number) }

    fun getView(id: UUID) = transaction { ms.getView(id) }

    fun new(module: Module, user: User) = transaction { ms.new(module, user) }

    fun update(module: Module, user: User) = transaction { ms.update(module, user) }
}
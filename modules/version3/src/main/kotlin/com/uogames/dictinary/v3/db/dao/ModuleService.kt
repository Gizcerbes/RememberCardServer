package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.addAndOp
import com.uogames.dictinary.v3.charLength
import com.uogames.dictinary.v3.db.dao.UserService.updateUser
import com.uogames.dictinary.v3.db.entity.*
import com.uogames.dictinary.v3.db.entity.Module.Companion.fromEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object ModuleService {

    private fun Query.buildWhere(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        first: Alias<PhraseTable>,
        second: Alias<PhraseTable>
    ): Query {
        return andWhere { ModuleTable.ban eq false }
            .addAndOp(text) { ModuleTable.name.lowerCase().like("%${it.lowercase()}%") }
            .addAndOp(langFirst) { first[PhraseTable.lang] eq it }
            .addAndOp(langSecond) { second[PhraseTable.lang] eq it }
            .addAndOp(countryFirst) { first[PhraseTable.country] eq it }
            .addAndOp(countrySecond) { second[PhraseTable.country] eq it }
    }

    fun count(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null
    ) = transaction {
        val first = PhraseTable.alias("pt1")
        val second = PhraseTable.alias("pt2")

        val query = ModuleTable
            .join(ModuleCardTable, JoinType.LEFT, ModuleTable.id, ModuleCardTable.moduleId)
            .join(CardTable, JoinType.LEFT, ModuleCardTable.cardId, CardTable.id)
            .join(first, JoinType.LEFT, CardTable.idPhrase, first[PhraseTable.id])
            .join(second, JoinType.LEFT, CardTable.idTranslate, second[PhraseTable.id])
            .slice(ModuleTable.id.countDistinct())
            .selectAll()
            .buildWhere(text, langFirst, langSecond, countryFirst, countrySecond, first, second)

        return@transaction query.first()[ModuleTable.id.countDistinct()]
    }

    fun get(id: UUID) = transaction {
        return@transaction ModuleEntity.findById(id)?.fromEntity()
    }

    fun get(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = transaction {
        val first = PhraseTable.alias("pt1")
        val second = PhraseTable.alias("pt2")
        val lengthAlias = ModuleTable.name.charLength().alias("len")

        val columns = ArrayList<Expression<*>>().apply {
            addAll(ModuleTable.columns)
            add(lengthAlias)
        }

        val query = ModuleTable
            .join(ModuleCardTable, JoinType.LEFT, ModuleTable.id, ModuleCardTable.moduleId)
            .join(CardTable, JoinType.LEFT, ModuleCardTable.cardId, CardTable.id)
            .join(first, JoinType.LEFT, CardTable.idPhrase, first[PhraseTable.id])
            .join(second, JoinType.LEFT, CardTable.idTranslate, second[PhraseTable.id])
            .slice(columns)
            .selectAll()
            .buildWhere(text, langFirst, langSecond, countryFirst, countrySecond, first, second)
            .orderBy(lengthAlias to SortOrder.ASC)
            .limit(1, number)
            .withDistinct()

        return@transaction query.firstOrNull()?.let { Module.fromRow(it) }
    }

    fun new(module: Module, user: User) = transaction { newModule(module, user) }

    private fun Transaction.newModule(module: Module, user: User): Module {
        updateUser(user)
        return ModuleEntity.new { update(module) }.fromEntity()
    }

    fun update(module: Module, user: User) = transaction {
        val loaded = ModuleEntity.findById(module.globalId)
        return@transaction if (loaded == null) {
            newModule(module, user)
        } else if (loaded.globalOwner.value == user.globalOwner) {
            updateUser(user)
            ModuleCardTable.deleteWhere { moduleId eq module.globalId }
            loaded.update(module)
            loaded.fromEntity()
        } else {
            null
        }
    }

}
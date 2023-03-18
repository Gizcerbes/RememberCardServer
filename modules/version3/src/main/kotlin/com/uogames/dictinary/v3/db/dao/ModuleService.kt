package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.addAndOp
import com.uogames.dictinary.v3.charLength
import com.uogames.dictinary.v3.db.entity.*
import com.uogames.dictinary.v3.db.entity.Module.Companion.fromEntity
import com.uogames.dictinary.v3.views.ModuleView
import org.jetbrains.exposed.dao.id.EntityID
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
    ): Long {
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

        return query.first()[ModuleTable.id.countDistinct()]
    }

    private fun getQuery(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ): Query {
        val first = PhraseTable.alias("pt1")
        val second = PhraseTable.alias("pt2")
        val lengthAlias = ModuleTable.name.charLength().alias("len")

        val columns = ArrayList<Expression<*>>().apply {
            addAll(ModuleTable.columns)
            add(lengthAlias)
        }

        return ModuleTable
            .join(ModuleCardTable, JoinType.LEFT, ModuleTable.id, ModuleCardTable.moduleId)
            .join(CardTable, JoinType.LEFT, ModuleCardTable.cardId, CardTable.id)
            .join(first, JoinType.LEFT, CardTable.idPhrase, first[PhraseTable.id])
            .join(second, JoinType.LEFT, CardTable.idTranslate, second[PhraseTable.id])
            .slice(columns)
            .selectAll()
            .buildWhere(text, langFirst, langSecond, countryFirst, countrySecond, first, second)
            .orderBy(
                lengthAlias to SortOrder.ASC,
                ModuleTable.name to SortOrder.ASC
            )
            .limit(1, number)
            .withDistinct()
    }

    fun get(id: UUID) = ModuleEntity.findById(id)?.fromEntity()


    fun get(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = getQuery(
        text = text,
        langFirst = langFirst,
        langSecond = langSecond,
        countryFirst = countryFirst,
        countrySecond = countrySecond,
        number = number
    ).firstOrNull()?.let { Module.fromRow(it) }

    fun getView(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = getQuery(
        text = text,
        langFirst = langFirst,
        langSecond = langSecond,
        countryFirst = countryFirst,
        countrySecond = countrySecond,
        number = number
    ).firstOrNull()?.let { ModuleView.fromRow(it) }

    fun getView(eID: EntityID<UUID>) = ModuleEntity[eID].let { ModuleView.fromEntity(it) }
    fun getView(id: UUID) = ModuleEntity.findById(id)?.let { ModuleView.fromEntity(it) }

    fun new(module: Module, user: User): Module {
        UserService.update(user)
        return ModuleEntity.new {
            update(module)
            ban = false
        }.fromEntity()
    }

    fun update(module: Module, user: User): Module? {
        val loaded = ModuleEntity.findById(module.globalId)
        return if (loaded == null) {
            new(module, user)
        } else if (loaded.globalOwner.value == user.globalOwner) {
            UserService.update(user)
            ModuleCardTable.deleteWhere { moduleId eq module.globalId }
            loaded.update(module)
            loaded.fromEntity()
        } else {
            null
        }
    }

    fun ban(
        moduleId: EntityID<UUID>,
        ban: Boolean
    ) {
        ModuleEntity.findById(moduleId)?.apply {
            this.ban = ban
        }
    }

}
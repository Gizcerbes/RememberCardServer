package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.addAndOp
import com.uogames.dictinary.v3.db.entity.*
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.countDistinct
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object ReportService {

    private fun Query.buildWhere(solved: Boolean?): Query{
        return addAndOp(solved){ ReportTable.solved eq it }
    }

    fun get(
        solved: Boolean? = null,
        number: Long
    ) = transaction {
        return@transaction ReportTable
            .selectAll()
            .buildWhere(solved)
            .limit(1, number)
            .first().let { Report.fromRow(it) }
    }

    fun count(
        solved: Boolean? = null
    ) = transaction {
        return@transaction ReportTable
            .slice(ReportTable.id.countDistinct())
            .selectAll()
            .buildWhere(solved)
            .first()[ReportTable.id.countDistinct()]
    }

    fun register(
        user: User,
        report: Report
    ) = transaction {
        UserService.update(user)
        ReportEntity.new {
            claimant = UserEntity[report.claimant].id
            message = report.message
            accused = UserEntity[report.accused].id
            idPhrase = report.idPhrase?.let { PhraseEntity[it].id }
            idCard = report.idCard?.let { CardEntity[it].id }
            idModule = report.idModule?.let { ModuleEntity[it].id }
            solved = false
        }
    }

    fun approve(
        reportID: UUID,
        ban: Boolean
    ) = transaction {
        val report = ReportEntity.findById(reportID)?.apply {
            solved = true
            if (ban) {
                UserService.strike(accused)
                idPhrase?.let { PhraseService.ban(it, true) }
                idCard?.let { CardService.ban(it, true) }
                idModule?.let { ModuleService.ban(it, true) }
            }
        }
    }


}
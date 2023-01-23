package com.uogames.dictinary.v3.db.entity

import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID


object ReportTable : UUIDTable(name = "report_table_v3") {
    val claimant = reference("claimant", UserTable.id, ReferenceOption.CASCADE)
    val message = text("message")
    val accused = reference("accused", UserTable.id, ReferenceOption.CASCADE)
    val idPhrase = reference("id_phrase", PhraseTable.id, ReferenceOption.SET_NULL).nullable()
    val idCard = reference("id_card", CardTable.id, ReferenceOption.SET_NULL).nullable()
    val idModule = reference("id_module", ModuleTable.id, ReferenceOption.SET_NULL).nullable()

}


class ReportEntity(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<ReportEntity>(ReportTable)

    var claimant by ReportTable.claimant
    var message by ReportTable.message
    var accused by ReportTable.accused
    var idPhrase by ReportTable.idPhrase
    var idCard by ReportTable.idCard
    var idModule by ReportTable.idModule

}

data class Report(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("claimant")
    var claimant: String,
    @SerializedName("message")
    var message: String,
    @SerializedName("accused")
    var accused: String,
    @SerializedName("id_phrase")
    var idPhrase: UUID? = null,
    @SerializedName("id_card")
    var idCard: UUID? = null,
    @SerializedName("id_module")
    var idModule: UUID? = null
) {

    companion object :TableMapper<ReportEntity, Report>{
        override fun fromRow(row: ResultRow) = Report(
            globalId = row[ReportTable.id].value,
            claimant = row[ReportTable.claimant].value,
            message = row[ReportTable.message],
            accused = row[ReportTable.accused].value,
            idPhrase = row[ReportTable.idPhrase]?.value,
            idCard = row[ReportTable.idCard]?.value,
            idModule = row[ReportTable.idModule]?.value
        )

        override fun ReportEntity.fromEntity() = Report(
            globalId = id.value,
            claimant = claimant.value,
            message = message,
            accused = accused.value,
            idPhrase = idPhrase?.value,
            idCard = idCard?.value,
            idModule = idModule?.value
        )

    }


}
package com.uogames.dictinary.v3.db.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID


object PronunciationTable : UUIDTable("pronunciation_table_v3", "global_id") {
    val globalOwner = reference("global_owner", UserTable.id, ReferenceOption.CASCADE)
    val audioUri = text("audio_uri")
    val ban = bool("ban")
}

class PronunciationEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PronunciationEntity>(PronunciationTable)

    var audioUri by PronunciationTable.audioUri
    var globalOwner by PronunciationTable.globalOwner
    var ban by PronunciationTable.ban

    fun update(pronounce: Pronunciation) {
        audioUri = pronounce.audioUri
        globalOwner = UserEntity[pronounce.globalOwner].id
    }

}

data class Pronunciation(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("global_owner")
    val globalOwner: String = "",
    @SerializedName("audio_uri")
    var audioUri: String,
    @Transient
    var ban: Boolean = false
) {

    companion object: TableMapper<PronunciationEntity, Pronunciation>{
        override fun fromRow(row: ResultRow) = Pronunciation(
            globalId = row[PronunciationTable.id].value,
            globalOwner = row[PronunciationTable.globalOwner].value,
            audioUri = row[PronunciationTable.audioUri],
            ban = row[PronunciationTable.ban]
        )

        override fun PronunciationEntity.fromEntity() = Pronunciation(
            globalId = id.value,
            audioUri = audioUri,
            globalOwner = globalOwner.value,
            ban = ban
        )

    }

}
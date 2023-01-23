package com.uogames.dictionary.db.entity.v1

import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID


object Pronunciations : UUIDTable("pronunciations", "global_id") {
    val globalOwner = reference("global_owner", Users.id, ReferenceOption.CASCADE)
    val audioUri = text("audio_uri")

}

class PronunciationEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PronunciationEntity>(Pronunciations)

    var audioUri by Pronunciations.audioUri
    var globalOwner by Pronunciations.globalOwner

    fun toPronunciation() = Pronunciation(
        globalId = id.value,
        audioUri = audioUri,
        globalOwner = globalOwner.value
    )

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
    var audioUri: String
)
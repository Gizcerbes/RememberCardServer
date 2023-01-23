package com.uogames.dictionary.db.entity.v1

import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

object Phrases : UUIDTable(name = "phrases", columnName = "global_id") {
    val globalOwner = reference("global_owner", Users.id, ReferenceOption.CASCADE)
    val phrase = text("phrase")
    val definition = text("definition").nullable()
    val lang = text("lang")
    val idPronounce = reference("pronounce_id", Pronunciations.id, ReferenceOption.SET_NULL).nullable()
    val idImage = reference("image_id", Images.id, ReferenceOption.SET_NULL).nullable()
    val timeChange = long("time_change")
    val like = long("like")
    val dislike = long("dislike")
}

class PhraseEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PhraseEntity>(Phrases)

    var globalOwner by Phrases.globalOwner
    var phrase by Phrases.phrase
    var definition by Phrases.definition
    var lang by Phrases.lang
    var idPronounce by Phrases.idPronounce
    var idImage by Phrases.idImage
    var timeChange by Phrases.timeChange
    var like by Phrases.like
    var dislike by Phrases.dislike

    fun toPhrase() = Phrase(
        phrase = phrase,
        definition = definition,
        lang = lang,
        idPronounce = idPronounce?.value,
        idImage = idImage?.value,
        timeChange = timeChange,
        like = like,
        dislike = dislike,
        globalId = id.value,
        globalOwner = globalOwner.value
    )

    fun update(phrase: Phrase) {
        this.phrase = phrase.phrase
        definition = phrase.definition
        lang = phrase.lang
        idPronounce = phrase.idPronounce?.let { PronunciationEntity.findById(it)?.id }
        idImage = phrase.idImage?.let { ImageEntity.findById(it)?.id }
        timeChange = phrase.timeChange
        like = phrase.like
        dislike = phrase.dislike
        globalOwner = UserEntity[phrase.globalOwner].id
    }

}

data class Phrase(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("global_owner")
    var globalOwner: String = "",
    @SerializedName("phrase")
    var phrase: String = "",
    @SerializedName("definition")
    var definition: String? = null,
    @SerializedName("lang")
    var lang: String = "eng-gb",
    @SerializedName("id_pronounce")
    var idPronounce: UUID? = null,
    @SerializedName("id_image")
    var idImage: UUID? = null,
    @SerializedName("time_change")
    var timeChange: Long = 0,
    @SerializedName("like")
    var like: Long = 0,
    @SerializedName("dislike")
    var dislike: Long = 0

)
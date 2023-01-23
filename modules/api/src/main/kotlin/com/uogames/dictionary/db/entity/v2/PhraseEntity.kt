package com.uogames.dictionary.db.entity.v2

import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

object Phrases : UUIDTable(name = "phrases_v2", columnName = "global_id") {
    val globalOwner = reference("global_owner", Users.id, ReferenceOption.CASCADE)
    val phrase = text("phrase")
    val definition = text("definition").nullable()
    val lang = varchar("lang", 100)
    val country = varchar("country", 100)
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
    var country by Phrases.country
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
        country = phrase.country
        idPronounce = phrase.idPronounce?.let { PronunciationEntity.findById(it)?.id }
        idImage = phrase.idImage?.let { ImageEntity.findById(it)?.id }
        timeChange = phrase.timeChange
        like = phrase.like
        dislike = phrase.dislike
        globalOwner = UserEntity[phrase.globalOwner].id
    }

}

data class Phrase(
    val globalId: UUID,
    var globalOwner: String = "",
    var phrase: String = "",
    var definition: String? = null,
    var lang: String = "eng",
    var country: String = "BELARUS",
    var idPronounce: UUID? = null,
    var idImage: UUID? = null,
    var timeChange: Long = 0,
    var like: Long = 0,
    var dislike: Long = 0
)
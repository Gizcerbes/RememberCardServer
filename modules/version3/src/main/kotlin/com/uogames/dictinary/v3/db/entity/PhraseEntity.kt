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

object PhraseTable : UUIDTable(name = "phrase_table_v3", columnName = "global_id") {
    val globalOwner = reference("global_owner", UserTable.id, ReferenceOption.CASCADE)
    val phrase = text("phrase")
    val definition = text("definition").nullable()
    val lang = varchar("lang", 100)
    val country = varchar("country", 100)
    val idPronounce = reference("pronounce_id", PronunciationTable.id, ReferenceOption.SET_NULL).nullable()
    val idImage = reference("image_id", ImageTable.id, ReferenceOption.SET_NULL).nullable()
    val timeChange = long("time_change")
    val like = long("like")
    val dislike = long("dislike")
    val ban = bool("ban")
}

class PhraseEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PhraseEntity>(PhraseTable)

    var globalOwner by PhraseTable.globalOwner
    var phrase by PhraseTable.phrase
    var definition by PhraseTable.definition
    var lang by PhraseTable.lang
    var country by PhraseTable.country
    var idPronounce by PhraseTable.idPronounce
    var idImage by PhraseTable.idImage
    var timeChange by PhraseTable.timeChange
    var like by PhraseTable.like
    var dislike by PhraseTable.dislike
    var ban by PhraseTable.ban

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
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("global_owner")
    var globalOwner: String = "",
    @SerializedName("phrase")
    var phrase: String = "",
    @SerializedName("definition")
    var definition: String? = null,
    @SerializedName("lang")
    var lang: String = "eng",
    @SerializedName("country")
    var country: String = "BELARUS",
    @SerializedName("id_pronounce")
    var idPronounce: UUID? = null,
    @SerializedName("id_image")
    var idImage: UUID? = null,
    @SerializedName("time_change")
    var timeChange: Long = 0,
    @SerializedName("like")
    var like: Long = 0,
    @SerializedName("dislike")
    var dislike: Long = 0,
    @Transient
    var ban: Boolean = false
) {

    companion object: TableMapper<PhraseEntity, Phrase>{
        override fun fromRow(row: ResultRow) = Phrase(
            globalId = row[PhraseTable.id].value,
            globalOwner = row[PhraseTable.globalOwner].value,
            phrase = row[PhraseTable.phrase],
            definition = row[PhraseTable.definition],
            lang = row[PhraseTable.lang],
            country = row[PhraseTable.country],
            idPronounce = row[PhraseTable.idPronounce]?.value,
            idImage = row[PhraseTable.idImage]?.value,
            timeChange = row[PhraseTable.timeChange],
            like = row[PhraseTable.like],
            dislike = row[PhraseTable.dislike],
            ban = row[PhraseTable.ban]
        )

        override fun PhraseEntity.fromEntity()= Phrase(
            phrase = phrase,
            definition = definition,
            lang = lang,
            idPronounce = idPronounce?.value,
            idImage = idImage?.value,
            timeChange = timeChange,
            like = like,
            dislike = dislike,
            globalId = id.value,
            globalOwner = globalOwner.value,
            ban = ban
        )

    }

}
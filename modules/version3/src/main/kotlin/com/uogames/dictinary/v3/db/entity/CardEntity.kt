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

object CardTable : UUIDTable(name = "card_table_v3", columnName = "global_id") {
    val globalOwner = reference("global_owner", UserTable.id, ReferenceOption.CASCADE)
    val idPhrase = reference("phrase_id", PhraseTable.id, ReferenceOption.CASCADE)
    val idTranslate = reference("translate_id", PhraseTable.id, ReferenceOption.CASCADE)
    val idImage = reference("image_id", ImageTable.id, ReferenceOption.SET_NULL).nullable()
    val reason = text("reason")
    val timeChange = long("time_chane")
    val like = long("like")
    val dislike = long("dislike")
    val ban = bool("ban")
}

class CardEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CardEntity>(CardTable)

    var globalOwner by CardTable.globalOwner
    var idPhrase by CardTable.idPhrase
    var idTranslate by CardTable.idTranslate
    var idImage by CardTable.idImage
    var reason by CardTable.reason
    var timeChange by CardTable.timeChange
    var like by CardTable.like
    var dislike by CardTable.dislike
    var ban by CardTable.ban


    fun update(card: Card) {
        globalOwner = UserEntity[card.globalOwner].id
        idPhrase = PhraseEntity[card.idPhrase].id
        idTranslate = PhraseEntity[card.idTranslate].id
        idImage = card.idImage?.let { ImageEntity.findById(it)?.id }
        reason = card.reason
        timeChange = card.timeChange
        like = card.like
        dislike = card.dislike
    }

}

data class Card(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("global_owner")
    var globalOwner: String = "",
    @SerializedName("id_phrase")
    var idPhrase: UUID,
    @SerializedName("id_translate")
    var idTranslate: UUID,
    @SerializedName("id_image")
    var idImage: UUID? = null,
    @SerializedName("reason")
    var reason: String = "",
    @SerializedName("time_change")
    var timeChange: Long = 0,
    @SerializedName("like")
    var like: Long = 0,
    @SerializedName("dislike")
    var dislike: Long = 0,
    @Transient
    var ban: Boolean = false
) {
    companion object: TableMapper<CardEntity, Card> {

        override fun fromRow(row: ResultRow) = Card(
            globalId = row[CardTable.id].value,
            globalOwner = row[CardTable.globalOwner].value,
            idPhrase =  row[CardTable.idPhrase].value,
            idTranslate = row[CardTable.idTranslate].value,
            idImage = row[CardTable.idImage]?.value,
            reason = row[CardTable.reason],
            timeChange = row[CardTable.timeChange],
            like = row[CardTable.like],
            dislike = row[CardTable.dislike],
            ban = row[CardTable.ban]
        )

        override fun CardEntity.fromEntity() = Card(
            globalId = id.value,
            globalOwner = globalOwner.value,
            idPhrase = idPhrase.value,
            idTranslate = idTranslate.value,
            idImage = idImage?.value,
            reason = reason,
            timeChange = timeChange,
            like = like,
            dislike = dislike,
            ban = ban
        )

    }
}
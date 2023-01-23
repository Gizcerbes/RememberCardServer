package com.uogames.dictionary.db.entity.v1

import com.google.gson.annotations.SerializedName
import com.uogames.dictionary.db.entity.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

object Cards : UUIDTable(name = "cards", columnName = "global_id") {
    val globalOwner = reference("global_owner", Users.id, ReferenceOption.CASCADE) //varchar("global_owner", 100).index()
    val idPhrase = reference("phrase_id", Phrases.id, ReferenceOption.CASCADE)
    val idTranslate = reference("translate_id", Phrases.id, ReferenceOption.CASCADE)
    val idImage = reference("image_id", Images.id, ReferenceOption.SET_NULL).nullable()
    val reason = text("reason")
    val timeChange = long("time_chane")
    val like = long("like")
    val dislike = long("dislike")
}

class CardEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CardEntity>(Cards)

    var globalOwner by Cards.globalOwner
    var idPhrase by Cards.idPhrase
    var idTranslate by Cards.idTranslate
    var idImage by Cards.idImage
    var reason by Cards.reason
    var timeChange by Cards.timeChange
    var like by Cards.like
    var dislike by Cards.dislike

    fun toCard() = Card(
        idPhrase = idPhrase.value,
        idTranslate = idTranslate.value,
        idImage = idImage?.value,
        reason = reason,
        timeChange = timeChange,
        like = like,
        dislike = dislike,
        globalId = id.value,
        globalOwner = globalOwner.value
    )

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
    var dislike: Long = 0
)
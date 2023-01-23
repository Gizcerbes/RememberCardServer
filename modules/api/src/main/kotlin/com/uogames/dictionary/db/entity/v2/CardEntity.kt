package com.uogames.dictionary.db.entity.v2

import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

object Cards : UUIDTable(name = "cards_v2", columnName = "global_id") {
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
    val globalId: UUID,
    var globalOwner: String = "",
    var idPhrase: UUID,
    var idTranslate: UUID,
    var idImage: UUID? = null,
    var reason: String = "",
    var timeChange: Long = 0,
    var like: Long = 0,
    var dislike: Long = 0
) {
    companion object {

        fun fromRow(row: ResultRow) = Card(
            globalId = row[Cards.id].value,
            globalOwner = row[Cards.globalOwner].value,
            idPhrase =  row[Cards.idPhrase].value,
            idTranslate = row[Cards.idTranslate].value,
            idImage = row[Cards.idImage]?.value,
            reason = row[Cards.reason],
            timeChange = row[Cards.timeChange],
            like = row[Cards.like],
            dislike = row[Cards.dislike]
        )

    }
}
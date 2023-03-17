package com.uogames.dictinary.v3.views

import com.google.gson.annotations.SerializedName
import com.uogames.dictinary.v3.db.dao.ImageService
import com.uogames.dictinary.v3.db.dao.PhraseService
import com.uogames.dictinary.v3.db.dao.UserService
import com.uogames.dictinary.v3.db.entity.CardEntity
import com.uogames.dictinary.v3.db.entity.CardTable
import com.uogames.dictinary.v3.db.entity.UserEntity
import com.uogames.dictinary.v3.db.entity.ViewMapper
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

data class CardView(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("user")
    var user: UserView,
    @SerializedName("phrase")
    var idPhrase: PhraseView,
    @SerializedName("translate")
    var idTranslate: PhraseView,
    @SerializedName("image")
    var idImage: ImageView? = null,
    @SerializedName("reason")
    var reason: String = "",
    @SerializedName("time_change")
    var timeChange: Long = 0,
    @SerializedName("like")
    var like: Long = 0,
    @SerializedName("dislike")
    var dislike: Long = 0
){

    companion object: ViewMapper<CardEntity, CardView> {
        override fun fromRow(row: ResultRow)= CardView(
            globalId = row[CardTable.id].value,
            user = row[CardTable.globalOwner].let { UserService.getView(it) },
            idPhrase =  row[CardTable.idPhrase].let { PhraseService.getView(it) },
            idTranslate = row[CardTable.idTranslate].let { PhraseService.getView(it) },
            idImage = row[CardTable.idImage]?.let { ImageService.getView(it) },
            reason = row[CardTable.reason],
            timeChange = row[CardTable.timeChange],
            like = row[CardTable.like],
            dislike = row[CardTable.dislike]
        )

        override fun fromEntity(entity: CardEntity) = CardView(
            globalId = entity.id.value,
            user = entity.globalOwner.let { UserService.getView(it) },
            idPhrase = entity.idPhrase.let { PhraseService.getView(it) },
            idTranslate = entity.idTranslate.let { PhraseService.getView(it) },
            idImage = entity.idImage?.let { ImageService.getView(it) },
            reason = entity.reason,
            timeChange = entity.timeChange,
            like = entity.like,
            dislike = entity.dislike
        )
    }

}
package com.uogames.dictinary.v3.views

import com.google.gson.annotations.SerializedName
import com.uogames.dictinary.v3.db.dao.ImageService
import com.uogames.dictinary.v3.db.dao.PhraseService
import com.uogames.dictinary.v3.db.dao.UserService
import com.uogames.dictinary.v3.db.entity.CardEntity
import com.uogames.dictinary.v3.db.entity.CardTable
import com.uogames.dictinary.v3.db.entity.CardViewEntity
import com.uogames.dictinary.v3.db.entity.ViewMapper
import com.uogames.dictinary.v3.views.CardView.Companion.toView
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

data class CardView(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("user")
    var user: UserView,
    @SerializedName("phrase")
    var phrase: PhraseView,
    @SerializedName("translate")
    var translate: PhraseView,
    @SerializedName("image")
    var image: ImageView? = null,
    @SerializedName("reason")
    var reason: String = "",
    @SerializedName("time_change")
    var timeChange: Long = 0,
    @SerializedName("like")
    var like: Long = 0,
    @SerializedName("dislike")
    var dislike: Long = 0
) {

    companion object : ViewMapper<CardEntity, CardView> {
        override fun fromRow(row: ResultRow) = CardView(
            globalId = row[CardTable.id].value,
            user = row[CardTable.globalOwner].let { UserService.getView(it) },
            phrase = row[CardTable.idPhrase].let { PhraseService.getView(it) },
            translate = row[CardTable.idTranslate].let { PhraseService.getView(it) },
            image = row[CardTable.idImage]?.let { ImageService.getView(it) },
            reason = row[CardTable.reason],
            timeChange = row[CardTable.timeChange],
            like = row[CardTable.like],
            dislike = row[CardTable.dislike]
        )

        override fun fromEntity(entity: CardEntity) = CardView(
            globalId = entity.id.value,
            user = entity.globalOwner.let { UserService.getView(it) },
            phrase = entity.idPhrase.let { PhraseService.getView(it) },
            translate = entity.idTranslate.let { PhraseService.getView(it) },
            image = entity.idImage?.let { ImageService.getView(it) },
            reason = entity.reason,
            timeChange = entity.timeChange,
            like = entity.like,
            dislike = entity.dislike
        )

        fun toView(viewEntity : CardViewEntity) = CardView(
            globalId = viewEntity.id.value,
            user = UserView.fromEntity(viewEntity.globalOwner),
            phrase = PhraseView.toView(viewEntity.idPhrase),
            translate = PhraseView.toView(viewEntity.idTranslate),
            image = viewEntity.idImage?.let { ImageView.fromEntity(it) },
            reason = viewEntity.reason,
            timeChange = viewEntity.timeChange,
            like = viewEntity.like,
            dislike = viewEntity.dislike
        )
    }

}
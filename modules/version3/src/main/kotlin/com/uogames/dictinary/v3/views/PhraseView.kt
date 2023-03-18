package com.uogames.dictinary.v3.views

import com.google.gson.annotations.SerializedName
import com.uogames.dictinary.v3.db.dao.ImageService
import com.uogames.dictinary.v3.db.dao.PronunciationService
import com.uogames.dictinary.v3.db.dao.UserService
import com.uogames.dictinary.v3.db.entity.PhraseEntity
import com.uogames.dictinary.v3.db.entity.PhraseTable
import com.uogames.dictinary.v3.db.entity.ViewMapper
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

data class PhraseView(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("user")
    var globalOwner: UserView,
    @SerializedName("phrase")
    var phrase: String = "",
    @SerializedName("definition")
    var definition: String? = null,
    @SerializedName("lang")
    var lang: String = "eng",
    @SerializedName("country")
    var country: String = "BELARUS",
    @SerializedName("pronounce")
    var pronounce: PronunciationView? = null,
    @SerializedName("image")
    var image: ImageView? = null,
    @SerializedName("time_change")
    var timeChange: Long = 0,
    @SerializedName("like")
    var like: Long = 0,
    @SerializedName("dislike")
    var dislike: Long = 0
) {

    companion object : ViewMapper<PhraseEntity, PhraseView>{
        override fun fromRow(row: ResultRow): PhraseView {
            return PhraseView(
                globalId = row[PhraseTable.id].value,
                globalOwner = row[PhraseTable.globalOwner].let { UserService.getView(it) },
                phrase = row[PhraseTable.phrase],
                definition = row[PhraseTable.definition],
                lang = row[PhraseTable.lang],
                country = row[PhraseTable.country],
                pronounce = row[PhraseTable.idPronounce]?.let { PronunciationService.getView(it) },
                image = row[PhraseTable.idImage]?.let { ImageService.getView(it) },
                timeChange = row[PhraseTable.timeChange],
                like = row[PhraseTable.like],
                dislike = row[PhraseTable.dislike]
            )
        }

        override fun fromEntity(entity: PhraseEntity): PhraseView {
            return PhraseView(
                phrase = entity.phrase,
                definition = entity.definition,
                lang = entity.lang,
                pronounce = entity.idPronounce?.let { PronunciationService.getView(it) },
                image = entity.idImage?.let { ImageService.getView(it) },
                timeChange = entity.timeChange,
                like = entity.like,
                dislike = entity.dislike,
                globalId = entity.id.value,
                globalOwner = entity.globalOwner.let { UserService.getView(it) }
            )
        }

    }

}
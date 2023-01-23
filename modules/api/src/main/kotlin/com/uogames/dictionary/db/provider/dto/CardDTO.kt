package com.uogames.dictionary.db.provider.dto

import com.google.gson.annotations.SerializedName
import com.uogames.dictionary.db.entity.v2.Card
import com.uogames.dictionary.db.provider.map.Mapper
import java.util.*

data class CardDTO(
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


package com.uogames.dictionary.db.provider.dto

import com.google.gson.annotations.SerializedName
import com.uogames.dictionary.db.entity.v2.Pronunciation
import com.uogames.dictionary.db.provider.map.Mapper
import java.util.*

data class PronunciationDTO(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("global_owner")
    val globalOwner: String = "",
    @SerializedName("audio_uri")
    var audioUri: String
)


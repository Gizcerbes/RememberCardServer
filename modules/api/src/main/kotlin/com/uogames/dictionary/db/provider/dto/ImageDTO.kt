package com.uogames.dictionary.db.provider.dto

import com.google.gson.annotations.SerializedName
import com.uogames.dictionary.db.entity.v2.Image
import com.uogames.dictionary.db.provider.map.Mapper
import java.util.*

data class ImageDTO(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("global_owner")
    val globalOwner: String = "",
    @SerializedName("image_uri")
    var imageUri: String
)


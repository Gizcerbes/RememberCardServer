package com.uogames.clientApi.version3.network.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class ImageResponse(
	@SerializedName("global_id")
	val globalId: UUID,
	@SerializedName("global_owner")
	val globalOwner: String = "",
	@SerializedName("image_uri")
	var imageUri: String
)
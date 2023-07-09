package com.uogames.clientApi.version2.network.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class PronunciationResponse(
	@SerializedName("global_id")
	val globalId: UUID,
	@SerializedName("global_owner")
	val globalOwner: String,
	@SerializedName("audio_uri")
	val audioUri: String
)
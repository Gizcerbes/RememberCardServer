package com.uogames.clientApi.version2.network.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
	@SerializedName("global_owner")
	val globalOwner: String,
	@SerializedName("name")
	val name: String
)
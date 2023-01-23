package com.uogames.dictionary.db.provider.dto

import com.google.gson.annotations.SerializedName
import com.uogames.dictionary.db.entity.v2.User
import com.uogames.dictionary.db.provider.map.Mapper

data class UserDTO(
    @SerializedName("global_owner")
    val globalOwner: String,
    @SerializedName("name")
    val name: String = ""
)


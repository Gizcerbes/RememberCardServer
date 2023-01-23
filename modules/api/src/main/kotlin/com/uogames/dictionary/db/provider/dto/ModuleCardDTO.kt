package com.uogames.dictionary.db.provider.dto

import com.google.gson.annotations.SerializedName
import com.uogames.dictionary.db.entity.v2.ModuleCard
import com.uogames.dictionary.db.provider.map.Mapper
import java.util.*

data class ModuleCardDTO(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("global_owner")
    var globalOwner: String = "",
    @SerializedName("module_id")
    var idModule: UUID,
    @SerializedName("card_id")
    var idCard: UUID
)



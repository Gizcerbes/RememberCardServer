package com.uogames.dictinary.v3.views

import com.google.gson.annotations.SerializedName
import java.util.*

data class ReportView(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("claimant")
    var claimant: UserView,
    @SerializedName("message")
    var message: String,
    @SerializedName("accused")
    var accused: UserView,
    @SerializedName("phrase")
    var idPhrase: PhraseView? = null,
    @SerializedName("card")
    var idCard: CardView? = null,
    @SerializedName("module")
    var idModule: ModuleView? = null
)
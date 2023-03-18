package com.uogames.dictinary.v3.views

import com.google.gson.annotations.SerializedName
import com.uogames.dictinary.v3.db.dao.UserService
import com.uogames.dictinary.v3.db.entity.PronunciationEntity
import com.uogames.dictinary.v3.db.entity.PronunciationTable
import com.uogames.dictinary.v3.db.entity.ViewMapper
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

data class PronunciationView(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("user")
    val globalOwner: UserView?,
    @SerializedName("audio_uri")
    var audioUri: String
) {

    companion object : ViewMapper<PronunciationEntity, PronunciationView> {
        override fun fromRow(row: ResultRow): PronunciationView {
            return PronunciationView(
                globalId = row[PronunciationTable.id].value,
                globalOwner = UserService.getView(row[PronunciationTable.globalOwner]),
                audioUri = row[PronunciationTable.audioUri]
            )
        }

        override fun fromEntity(entity: PronunciationEntity): PronunciationView {
            return PronunciationView(
                globalId = entity.id.value,
                globalOwner = UserService.getView(entity.globalOwner),
                audioUri = entity.audioUri
            )
        }
    }


}
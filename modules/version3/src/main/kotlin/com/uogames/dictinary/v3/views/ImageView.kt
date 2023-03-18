package com.uogames.dictinary.v3.views

import com.google.gson.annotations.SerializedName
import com.uogames.dictinary.v3.db.dao.UserService
import com.uogames.dictinary.v3.db.entity.ImageEntity
import com.uogames.dictinary.v3.db.entity.ImageTable
import com.uogames.dictinary.v3.db.entity.ViewMapper
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

data class ImageView(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("user")
    val globalOwner: UserView,
    @SerializedName("image_uri")
    var imageUri: String
){

    companion object :ViewMapper<ImageEntity, ImageView> {
        override fun fromRow(row: ResultRow): ImageView {
            return ImageView(
                globalId = row[ImageTable.id].value,
                globalOwner = UserService.getView(row[ImageTable.globalOwner]),
                imageUri = row[ImageTable.imageUri]
            )
        }

        override fun fromEntity(entity: ImageEntity): ImageView {
            return ImageView(
                globalId = entity.id.value,
                globalOwner = UserService.getView(entity.globalOwner),
                imageUri = entity.imageUri
            )
        }
    }

}

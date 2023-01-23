package com.uogames.dictionary.db.entity.v2

import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

object Images : UUIDTable("images_v2", "global_id") {
    val globalOwner = reference("global_owner", Users.id, ReferenceOption.CASCADE)
    val imageUri = text("image_uri")
}

class ImageEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ImageEntity>(Images)

    var imageUri by Images.imageUri
    var globalOwner by Images.globalOwner

    fun toImage() = Image(
        imageUri = imageUri,
        globalId = id.value,
        globalOwner = globalOwner.value
    )

    fun update(image: Image) {
        imageUri = image.imageUri
        globalOwner = UserEntity[image.globalOwner].id
    }

}

data class Image(
    val globalId: UUID,
    val globalOwner: String = "",
    var imageUri: String
)

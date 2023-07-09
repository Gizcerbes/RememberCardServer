package com.uogames.dictinary.v3.db.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

object ImageTable : UUIDTable("image_table", "global_id") {
    val globalOwner = reference("global_owner", UserTable.id, ReferenceOption.CASCADE)
    val imageUri = text("image_uri")
    val ban = bool("ban")
}

class ImageEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ImageEntity>(ImageTable)

    var imageUri by ImageTable.imageUri
    var globalOwner by ImageTable.globalOwner
    var ban by ImageTable.ban


    fun update(image: Image) {
        imageUri = image.imageUri
        globalOwner = UserEntity[image.globalOwner].id
    }

}

data class Image(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("global_owner")
    var globalOwner: String = "",
    @SerializedName("image_uri")
    var imageUri: String,
    @Transient
    var ban: Boolean = false
) {
    companion object: TableMapper<ImageEntity, Image>{
        override fun fromRow(row: ResultRow) = Image(
            globalId = row[ImageTable.id].value,
            globalOwner = row[ImageTable.globalOwner].value,
            imageUri = row[ImageTable.imageUri],
            ban = row[ImageTable.ban]
        )

        override fun ImageEntity.fromEntity() = Image(
            imageUri = imageUri,
            globalId = id.value,
            globalOwner = globalOwner.value,
            ban = ban
        )

    }
}

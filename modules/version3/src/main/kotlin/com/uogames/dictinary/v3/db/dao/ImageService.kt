package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.db.entity.*
import com.uogames.dictinary.v3.db.entity.Image.Companion.fromEntity
import com.uogames.dictinary.v3.views.ImageView
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.UUID

object ImageService {

    fun get(imageID: UUID) = ImageEntity.findById(imageID)?.fromEntity()

    fun getView(eID: EntityID<UUID>) = ImageEntity[eID].let { ImageView.fromEntity(it) }

    fun getView(imageID: UUID) = ImageEntity
        .findById(imageID)?.let { ImageView.fromEntity(it) }

    fun new(image: Image, user: User): Image {
        UserService.update(user)
        return ImageEntity.new {
            update(image)
            ban = false
        }.fromEntity()
    }

    fun update(image: Image, user: User): Image? {
        UserService.update(user)
        return ImageEntity.findById(image.globalId)?.apply { update(image) }?.fromEntity()
    }

    fun delete(id: UUID) = ImageEntity.findById(id)?.delete()

    fun cleanImage(id: UUID) {
        val count = ImageTable
            .join(PhraseTable, JoinType.LEFT, ImageTable.id, PhraseTable.idImage)
            .join(CardTable, JoinType.LEFT, ImageTable.id, CardTable.idImage)
            .slice(ImageTable.id.countDistinct())
            .select((PhraseTable.idImage eq id) or (CardTable.idImage eq id))
            .first()[ImageTable.id.countDistinct()]
        if (count == 0L) delete(id)
    }

}
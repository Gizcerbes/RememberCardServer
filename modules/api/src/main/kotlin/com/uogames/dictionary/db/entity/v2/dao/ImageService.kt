package com.uogames.dictionary.db.entity.v2.dao

import com.uogames.dictionary.db.entity.v2.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object ImageService {

    fun get(imageID: UUID) = transaction {
        return@transaction ImageEntity.findById(imageID)?.toImage()
    }

    fun new(image: Image, user: User) = transaction {
        UserService.update(user)
        return@transaction ImageEntity.new {
            update(image)
        }.toImage()
    }

    fun update(image: Image, user: User) = transaction {
        UserService.update(user)
        return@transaction ImageEntity.findById(image.globalId)?.apply {
            update(image)
        }?.toImage()
    }

    fun delete(id: UUID) = transaction {
        ImageEntity.findById(id)?.delete()
    }

}
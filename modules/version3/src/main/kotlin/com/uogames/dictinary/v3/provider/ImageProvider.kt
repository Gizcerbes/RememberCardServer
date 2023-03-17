package com.uogames.dictinary.v3.provider

import com.uogames.dictinary.v3.db.dao.ImageService
import com.uogames.dictinary.v3.db.entity.Image
import com.uogames.dictinary.v3.db.entity.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object ImageProvider {

    private val imageService = ImageService

    fun get(imageID: UUID) = transaction { imageService.get(imageID) }

    fun getView(imageID: UUID) = transaction { imageService.getView(imageID) }

    fun new(image: Image, user: User) = transaction { imageService.new(image, user) }

    fun update(image: Image, user: User) = transaction { imageService.update(image, user) }

    fun delete(id: UUID) = transaction { imageService.delete(id) }

    fun cleanImage(id: UUID) = transaction { imageService.cleanImage(id) }

}
package com.uogames.dictionary.db.provider

import com.uogames.dictionary.db.entity.v2.dao.ImageService
import com.uogames.dictionary.db.provider.dto.ImageDTO
import com.uogames.dictionary.db.provider.dto.UserDTO
import com.uogames.dictionary.db.provider.map.v2.ImageMap
import com.uogames.dictionary.db.provider.map.v2.UserMap
import java.util.UUID

object ImageProvider {

    private val service = ImageService
    private val imageMap = ImageMap
    private val userMap = UserMap

    fun get(imageID: UUID) = service.get(
        imageID
    )?.let { imageMap.toDTO(it) }

    fun new(image: ImageDTO, user: UserDTO) = service.new(
        imageMap.toEntity(image),
        userMap.toEntity(user)
    ).let { imageMap.toDTO(it) }

    fun update(image: ImageDTO, user: UserDTO) = service.update(
        imageMap.toEntity(image),
        userMap.toEntity(user)
    )?.let { imageMap.toDTO(it) }

    fun delete(id: UUID) = service.delete(id)

}
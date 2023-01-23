package com.uogames.dictionary.db.provider.map.v2

import com.uogames.dictionary.db.entity.v2.Image
import com.uogames.dictionary.db.provider.dto.ImageDTO
import com.uogames.dictionary.db.provider.map.Mapper

object ImageMap : Mapper<Image, ImageDTO> {
    override fun toDTO(entity: Image) = ImageDTO(
        globalId = entity.globalId,
        globalOwner = entity.globalOwner,
        imageUri = entity.imageUri
    )

    override fun toEntity(dto: ImageDTO) = Image(
        globalId = dto.globalId,
        globalOwner = dto.globalOwner,
        imageUri = dto.imageUri
    )

}
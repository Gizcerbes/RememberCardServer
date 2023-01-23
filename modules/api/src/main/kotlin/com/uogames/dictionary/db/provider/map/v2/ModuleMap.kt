package com.uogames.dictionary.db.provider.map.v2

import com.uogames.dictionary.db.entity.v2.Module
import com.uogames.dictionary.db.provider.dto.ModuleDTO
import com.uogames.dictionary.db.provider.map.Mapper

object ModuleMap: Mapper<Module, ModuleDTO> {
    override fun toDTO(entity: Module) = ModuleDTO(
        globalId = entity.globalId,
        globalOwner = entity.globalOwner,
        name = entity.name,
        timeChange = entity.timeChange,
        like = entity.like,
        dislike = entity.dislike
    )

    override fun toEntity(dto: ModuleDTO) = Module(
        globalId = dto.globalId,
        globalOwner = dto.globalOwner,
        name = dto.name,
        timeChange = dto.timeChange,
        like = dto.like,
        dislike = dto.dislike
    )

}
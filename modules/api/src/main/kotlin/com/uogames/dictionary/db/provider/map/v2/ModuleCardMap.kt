package com.uogames.dictionary.db.provider.map.v2

import com.uogames.dictionary.db.entity.v2.ModuleCard
import com.uogames.dictionary.db.provider.dto.ModuleCardDTO
import com.uogames.dictionary.db.provider.map.Mapper

object ModuleCardMap: Mapper<ModuleCard, ModuleCardDTO> {
    override fun toDTO(entity: ModuleCard) = ModuleCardDTO(
        globalId = entity.globalId,
        globalOwner = entity.globalOwner,
        idModule = entity.idModule,
        idCard = entity.idCard
    )

    override fun toEntity(dto: ModuleCardDTO)= ModuleCard(
        globalId = dto.globalId,
        globalOwner = dto.globalOwner,
        idModule = dto.idModule,
        idCard = dto.idCard
    )

}
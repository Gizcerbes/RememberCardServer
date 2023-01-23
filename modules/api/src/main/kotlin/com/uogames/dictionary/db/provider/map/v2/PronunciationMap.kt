package com.uogames.dictionary.db.provider.map.v2

import com.uogames.dictionary.db.entity.v2.Pronunciation
import com.uogames.dictionary.db.provider.dto.PronunciationDTO
import com.uogames.dictionary.db.provider.map.Mapper

object PronunciationMap: Mapper<Pronunciation, PronunciationDTO> {
    override fun toDTO(entity: Pronunciation) =  PronunciationDTO (
        globalId = entity.globalId,
        globalOwner = entity.globalOwner,
        audioUri = entity.audioUri
    )

    override fun toEntity(dto: PronunciationDTO) = Pronunciation(
        globalId = dto.globalId,
        globalOwner = dto.globalOwner,
        audioUri = dto.audioUri
    )

}
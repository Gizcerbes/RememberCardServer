package com.uogames.dictionary.db.provider.map.v2

import com.uogames.dictionary.db.entity.v2.Card
import com.uogames.dictionary.db.provider.dto.CardDTO
import com.uogames.dictionary.db.provider.map.Mapper

object CardMap : Mapper<Card, CardDTO> {
    override fun toDTO(entity: Card) = CardDTO(
        globalId = entity.globalId,
        globalOwner = entity.globalOwner,
        idPhrase = entity.idPhrase,
        idTranslate = entity.idTranslate,
        idImage = entity.idImage,
        reason = entity.reason,
        timeChange = entity.timeChange,
        like = entity.like,
        dislike = entity.dislike
    )

    override fun toEntity(dto: CardDTO) = Card(
        globalId = dto.globalId,
        globalOwner = dto.globalOwner,
        idPhrase = dto.idPhrase,
        idTranslate = dto.idTranslate,
        idImage = dto.idImage,
        reason = dto.reason,
        timeChange = dto.timeChange,
        like = dto.like,
        dislike = dto.dislike
    )

}
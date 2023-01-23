package com.uogames.dictionary.db.provider.map.v2

import com.uogames.dictionary.db.entity.v2.Phrase
import com.uogames.dictionary.db.provider.dto.PhraseDTO
import com.uogames.dictionary.db.provider.map.Mapper

object PhraseMap : Mapper<Phrase, PhraseDTO> {
    override fun toDTO(entity: Phrase) = PhraseDTO(
        globalId = entity.globalId,
        globalOwner = entity.globalOwner,
        phrase = entity.phrase,
        definition = entity.definition,
        lang = entity.lang,
        country = entity.country,
        idPronounce = entity.idPronounce,
        idImage = entity.idImage,
        timeChange = entity.timeChange,
        like = entity.like,
        dislike = entity.dislike
    )

    override fun toEntity(dto: PhraseDTO) = Phrase(
        globalId = dto.globalId,
        globalOwner = dto.globalOwner,
        phrase = dto.phrase,
        definition = dto.definition,
        lang = dto.lang,
        country = dto.country,
        idPronounce = dto.idPronounce,
        idImage = dto.idImage,
        timeChange = dto.timeChange,
        like = dto.like,
        dislike = dto.dislike
    )

}
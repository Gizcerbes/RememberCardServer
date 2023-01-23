package com.uogames.dictionary.db.provider

import com.uogames.dictionary.db.entity.v2.dao.PhraseService
import com.uogames.dictionary.db.provider.dto.PhraseDTO
import com.uogames.dictionary.db.provider.dto.UserDTO
import com.uogames.dictionary.db.provider.map.v2.PhraseMap
import com.uogames.dictionary.db.provider.map.v2.UserMap
import java.util.UUID

object PhraseProvider {

    private val service = PhraseService
    private val phraseMap = PhraseMap
    private val userMap = UserMap

    fun count(text:String) = service.count(text)

    fun get(id:UUID) = service.get(id)

    fun get(text: String, position: Long) = service.get(
        text,
        position
    )?.let { phraseMap.toDTO(it) }

    fun new(phrase: PhraseDTO, user: UserDTO) = service.new(
        phraseMap.toEntity(phrase),
        userMap.toEntity(user)
    ).let { phraseMap.toDTO(it) }

    fun update(phrase: PhraseDTO, user: UserDTO) = service.update(
        phraseMap.toEntity(phrase),
        userMap.toEntity(user)
    )?.let { phraseMap.toDTO(it) }

}
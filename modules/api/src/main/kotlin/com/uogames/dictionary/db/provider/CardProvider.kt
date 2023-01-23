package com.uogames.dictionary.db.provider

import com.uogames.dictionary.db.entity.v2.dao.CardService
import com.uogames.dictionary.db.provider.dto.CardDTO
import com.uogames.dictionary.db.provider.dto.UserDTO
import com.uogames.dictionary.db.provider.map.v2.CardMap
import com.uogames.dictionary.db.provider.map.v2.UserMap
import java.util.UUID

object CardProvider {

    private val service = CardService
    private val cardMap = CardMap
    private val userMap = UserMap

    fun count(text: String) = service.countV2(text)

    fun get(id: UUID) = service.get(id)?.let { cardMap.toDTO(it) }

    fun get(text: String, position: Long) = service.get(
        text,
        position
    )?.let { cardMap.toDTO(it) }

    fun new(card: CardDTO, user: UserDTO) = service.new(
        cardMap.toEntity(card),
        userMap.toEntity(user)
    ).let { cardMap.toDTO(it) }

    fun update(card: CardDTO, user: UserDTO) = service.update(
        cardMap.toEntity(card),
        userMap.toEntity(user)
    )?.let { cardMap.toDTO(it) }

}
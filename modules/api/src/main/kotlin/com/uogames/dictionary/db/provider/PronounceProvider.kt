package com.uogames.dictionary.db.provider

import com.uogames.dictionary.db.entity.v2.dao.PronunciationService
import com.uogames.dictionary.db.provider.dto.PronunciationDTO
import com.uogames.dictionary.db.provider.dto.UserDTO
import com.uogames.dictionary.db.provider.map.v2.PronunciationMap
import com.uogames.dictionary.db.provider.map.v2.UserMap
import java.util.UUID

object PronounceProvider {

    private val service = PronunciationService
    private val pronunciationMap = PronunciationMap
    private val userMap = UserMap

    fun get(id: UUID) = service.get(id)

    fun new(pronunciation: PronunciationDTO, user: UserDTO) = service.new(
        pronunciationMap.toEntity(pronunciation),
        userMap.toEntity(user)
    ).let { pronunciationMap.toDTO(it) }

    fun update(pronunciation: PronunciationDTO, user: UserDTO) = service.update(
        pronunciationMap.toEntity(pronunciation),
        userMap.toEntity(user)
    )?.let { pronunciationMap.toDTO(it) }

    fun delete(id: UUID) = service.delete(id)

}
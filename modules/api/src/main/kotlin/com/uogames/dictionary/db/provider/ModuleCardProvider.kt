package com.uogames.dictionary.db.provider

import com.uogames.dictionary.db.entity.v2.dao.ModuleCardService
import com.uogames.dictionary.db.provider.dto.ModuleCardDTO
import com.uogames.dictionary.db.provider.dto.UserDTO
import com.uogames.dictionary.db.provider.map.v2.ModuleCardMap
import com.uogames.dictionary.db.provider.map.v2.UserMap
import java.util.UUID

object ModuleCardProvider {

    private val service = ModuleCardService
    private val moduleCardMap = ModuleCardMap
    private val userMap = UserMap

    fun count(moduleID:UUID) = service.count(moduleID)

    fun get(id: UUID) = service.get(id)

    fun get(moduleID: UUID, position: Long) = service.get(
        moduleID,
        position
    )?.let { moduleCardMap.toDTO(it) }

    fun new(moduleCard: ModuleCardDTO, user: UserDTO) = service.new(
        moduleCardMap.toEntity(moduleCard),
        userMap.toEntity(user)
    ).let { moduleCardMap.toDTO(it) }

    fun update(moduleCard: ModuleCardDTO, user: UserDTO) = service.update(
        moduleCardMap.toEntity(moduleCard),
        userMap.toEntity(user)
    )?.let { moduleCardMap.toDTO(it) }

}
package com.uogames.dictionary.db.provider

import com.uogames.dictionary.db.entity.v2.dao.ModuleService
import com.uogames.dictionary.db.provider.dto.ModuleDTO
import com.uogames.dictionary.db.provider.dto.UserDTO
import com.uogames.dictionary.db.provider.map.v2.ModuleMap
import com.uogames.dictionary.db.provider.map.v2.UserMap
import java.util.UUID

object ModuleProvider {

    private val service = ModuleService
    private val moduleMap = ModuleMap
    private val userMap = UserMap


    fun count(text: String) = service.count(text)

    fun get(id: UUID) = service.get(id)

    fun get(text: String, position: Long) = service.get(
        text,
        position
    )

    fun new(module: ModuleDTO, user: UserDTO) = service.new(
        moduleMap.toEntity(module),
        userMap.toEntity(user)
    ).let { moduleMap.toDTO(it) }

    fun update(module: ModuleDTO, user: UserDTO) = service.update(
        moduleMap.toEntity(module),
        userMap.toEntity(user)
    )?.let { moduleMap.toDTO(it) }

}
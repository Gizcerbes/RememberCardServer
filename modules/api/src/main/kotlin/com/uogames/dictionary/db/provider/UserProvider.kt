package com.uogames.dictionary.db.provider

import com.uogames.dictionary.db.entity.v2.dao.UserService
import com.uogames.dictionary.db.provider.dto.UserDTO
import com.uogames.dictionary.db.provider.map.v2.UserMap

object UserProvider {

    private val service = UserService
    private val userMap = UserMap

    fun get(id: String) = service.get(id)

    fun update(user: UserDTO) = service.update(
        userMap.toEntity(user)
    ).let { userMap.toDTO(it) }

}
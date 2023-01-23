package com.uogames.dictionary.db.provider.map.v2

import com.uogames.dictionary.db.entity.v2.User
import com.uogames.dictionary.db.provider.dto.UserDTO
import com.uogames.dictionary.db.provider.map.Mapper

object UserMap: Mapper<User, UserDTO> {
    override fun toDTO(user: User) = UserDTO(
        globalOwner = user.globalOwner,
        name = user.name
    )

    override fun toEntity(dto: UserDTO) = User(
        globalOwner = dto.globalOwner,
        name = dto.name
    )

}
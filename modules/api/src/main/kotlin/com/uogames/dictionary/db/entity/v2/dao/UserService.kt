package com.uogames.dictionary.db.entity.v2.dao

import com.uogames.dictionary.db.entity.v2.*
import com.uogames.dictionary.service.ifNull
import org.jetbrains.exposed.sql.transactions.transaction

object UserService {

    fun get(uid: String) = transaction {
        return@transaction UserEntity.findById(uid)?.toUser()
    }


    fun update(user: User) = transaction {
        UserEntity.findById(user.globalOwner).ifNull {
            UserEntity.new(user.globalOwner) { name = ""}
        }.apply {
            name = user.name
        }.toUser()
    }


}
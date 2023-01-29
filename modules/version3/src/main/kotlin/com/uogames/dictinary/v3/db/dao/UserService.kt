package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.db.entity.UserEntity
import com.uogames.dictinary.v3.ifNull
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

object UserService {

    fun get(uid: String) = transaction {
        return@transaction UserEntity.findById(uid)?.toUser()
    }


    fun update(user: User) = transaction { updateUser(user) }

    fun Transaction.updateUser(user: User) {
        UserEntity.findById(user.globalOwner).ifNull {
            UserEntity.new(user.globalOwner) { name = "" }
        }.apply {
            name = user.name
        }.toUser()
    }


}
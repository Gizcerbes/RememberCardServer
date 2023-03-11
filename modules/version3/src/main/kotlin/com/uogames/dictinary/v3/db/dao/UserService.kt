package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.db.entity.UserEntity
import com.uogames.dictinary.v3.ifNull
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

object UserService {

    fun get(uid: String) = transaction {
        return@transaction UserEntity.findById(uid)?.toUser()
    }


    fun update(user: User) = transaction { updateUser(user) }

    fun Transaction.updateUser(user: User) {
        UserEntity.findById(user.globalOwner).ifNull {
            UserEntity.new(user.globalOwner) {
                name = ""
                strike = 0
                ban = false
            }
        }.apply {
            name = user.name
        }.toUser()
    }

    fun strike(userId: String) = transaction { userStrike(userId) }

    fun Transaction.userStrike(userId: EntityID<String>) = userStrike(userId.value)

    fun Transaction.userStrike(userId: String) {
        UserEntity.findById(userId)?.apply {
            strike++
            if (strike > 10) ban = true
        }
    }


}
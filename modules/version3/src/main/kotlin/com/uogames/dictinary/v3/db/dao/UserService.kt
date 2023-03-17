package com.uogames.dictinary.v3.db.dao

import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.db.entity.UserEntity
import com.uogames.dictinary.v3.ifNull
import com.uogames.dictinary.v3.views.UserView
import org.jetbrains.exposed.dao.id.EntityID

object UserService {

    fun get(uid: String) = UserEntity.findById(uid)?.toUser()

    fun getView(uid: EntityID<String>) = UserEntity[uid].let { UserView.fromEntity(it) }
    fun getView(uid: String) = UserEntity.findById(uid)?.let { UserView.fromEntity(it) }

    fun update(user: User) = UserEntity.findById(user.globalOwner).ifNull {
        UserEntity.new(user.globalOwner) {
            name = ""
            strike = 0
            ban = false
        }
    }.apply {
        name = user.name
    }.toUser()


    fun strike(userId: EntityID<String>) = strike(userId.value)

    fun strike(userId: String) {
        UserEntity.findById(userId)?.apply {
            strike++
            if (strike > 10) ban = true
        }
    }


}
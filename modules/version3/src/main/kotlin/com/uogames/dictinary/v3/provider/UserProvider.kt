package com.uogames.dictinary.v3.provider

import com.uogames.dictinary.v3.db.dao.UserService
import com.uogames.dictinary.v3.db.entity.User
import org.jetbrains.exposed.sql.transactions.transaction

object UserProvider {

    private val us = UserService

    fun get(uid: String) = transaction { us.get(uid) }
    fun getView(uid: String) = transaction { us.getView(uid) }

    fun update(user: User) = transaction { us.update(user) }

    fun strike(uid: String) = transaction { us.strike(uid) }



}
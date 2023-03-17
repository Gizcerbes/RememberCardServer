package com.uogames.dictinary.v3.db.entity

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Transaction

interface ViewMapper<T, R> {

    fun fromRow(row: ResultRow): R

    fun fromEntity(entity: T): R

}
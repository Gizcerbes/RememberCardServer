package com.uogames.dictinary.v3.db.entity

import org.jetbrains.exposed.sql.ResultRow

interface TableMapper<T,R> {

    fun fromRow(row: ResultRow): R

    fun T.fromEntity():R


}
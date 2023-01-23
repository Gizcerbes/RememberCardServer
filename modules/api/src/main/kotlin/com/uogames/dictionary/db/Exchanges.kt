package com.uogames.dictionary.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Function


class CharLength<T : Comparable<T>, in S : T?>(
    private val expr: Expression<in S>,
    private val _columnType: IColumnType
) : Function<T?>(_columnType) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        append("CHAR_LENGTH(", expr, ")")
    }
}

fun <T : Comparable<T>, S : T?> ExpressionWithColumnType<in S>.charLength(): CharLength<T, S> =
    CharLength<T, S>(this, this.columnType)

fun <T> Query.addAndOp(param: T?, opBuilder: (T) -> Op<Boolean>): Query {
    return if (param != null) andWhere { opBuilder(param) } else this
}



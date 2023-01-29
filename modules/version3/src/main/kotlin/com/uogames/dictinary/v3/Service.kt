package com.uogames.dictinary.v3

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Function
import java.util.*

inline fun <C> C?.ifNull(defaultValue: () -> C): C =
    this ?: defaultValue()

inline fun <C : CharSequence?> C?.ifNullOrEmpty(defaultValue: () -> C): C {
    return if (isNullOrEmpty()) defaultValue()
    else this
}
fun String?.toLongOrDefault(def: Long): Long {
    if (this == null) return def
    return try {
        toLong()
    } catch (e: Exception) {
        def
    }
}

val defaultUUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

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
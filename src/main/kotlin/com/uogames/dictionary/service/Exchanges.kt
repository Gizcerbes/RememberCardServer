package com.uogames.dictionary.service

import io.ktor.server.application.*
import io.ktor.server.request.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Function
import java.util.*


fun ApplicationCall.printHeader() {
    println("| ********* HEADER *********")
    request.headers.forEach { s, strings ->
        println("| $s = $strings")
    }
    println("| ************************\n")
}

fun ApplicationCall.printParams() {
    println("| ******** PARAMS ********")
    parameters.forEach { s, strings ->
        println("| $s = $strings")
    }
    println("| **********************\n")
}

suspend fun ApplicationCall.printBody() {
    println("| ********* BODY *********")
    try {
        receiveStream().use {
            while (true) {
                val byte = it.read()
                if (byte == -1) break
                print(byte.toChar())
            }
            println()
        }
    } catch (e: Exception) {
        System.err.println("| body $e")
    }
    println("| ************************\n")
}

val defaultUUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

fun String?.toLong(default: Long): Long {
    return if (this == null) default
    else try {
        toLong()
    } catch (e: Exception) {
        default
    }
}

inline fun <C> C?.ifNull(defaultValue: () -> C): C =
    this ?: defaultValue()



inline fun <C : CharSequence?> C?.ifNullOrEmpty(defaultValue: () -> C): C {
    return if (isNullOrEmpty()) defaultValue()
    else this
}

inline fun Boolean.ifTrue(body: () -> Unit): Boolean {
    if (this) body()
    return this
}

inline fun Boolean.ifFalse(body: () -> Unit): Boolean {
    if (!this) body()
    return this
}

inline fun <T, R> T.runChecking(check: (T) -> Boolean, body: () -> R): R? {
    return if (check(this)) body()
    else null
}





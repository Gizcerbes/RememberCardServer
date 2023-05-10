package com.uogames.dictinary.v3

import io.ktor.server.application.*
import io.ktor.server.request.*
import java.util.*

object Exchanges {
    val defaultUUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

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
}

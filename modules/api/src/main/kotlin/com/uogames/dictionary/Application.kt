package com.uogames.dictionary

import com.uogames.dictinary.v3.plugins.configV3
import com.uogames.dictionary.db.initDB
import com.uogames.dictionary.plugins.configureRouting
import com.uogames.dictionary.service.JWTBuilder
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*

fun main(args: Array<String>): Unit = io.ktor.server.tomcat.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {


    val secret = environment.config.property("jwt.secret").getString()

    initDB()

    install(ContentNegotiation) {
        gson {  }
    }

    val timeLive = 60000
    install(Authentication) {
        jwt("auth-jwt") {
            validate { credential ->
                val stringMap = credential.payload.getClaim("stringMap").asMap() ?: return@validate null
                val time = System.currentTimeMillis() - credential.payload.expiresAt.time
                if (time > timeLive) {
                    println("$time and $timeLive")
                    return@validate null
                }
                val identifier = stringMap["Identifier"]?.toString() ?: return@validate null
                val userUid = stringMap["User UID"]?.toString() ?: return@validate null
                if (identifier.isEmpty() or userUid.isEmpty()) return@validate null
                JWTPrincipal(credential.payload)
            }
            verifier {
                JWTBuilder.check(secret, it.render().replace(it.authScheme, "").trim())
            }
        }
    }

    configureRouting()

    configV3()

}

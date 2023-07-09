package com.uogames.dictionary

import com.uogames.dictinary.v3.plugins.configV3
import com.uogames.dictionary.db.initDB
import com.uogames.dictionary.service.JWTBuilder
import com.uogames.test.test
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.tomcat.*

fun main(args: Array<String>) {
    val applicationEnvironment = commandLineEnvironment(args)
    val engine = TomcatApplicationEngine(applicationEnvironment) {
        val deploymentConfig = applicationEnvironment.config.config("ktor.deployment")
        loadCommonConfiguration(deploymentConfig)
    }
    engine.start(true)
}

fun Application.module() {

    val secret = environment.config.property("jwt.secret").getString()
    val volume = environment.config.property("ktor.rootDir").getString()
    val dbConfig = environment.config.property("ktor.hikariConfig").getString()
    val rootPath = environment.rootPath

    initDB(dbConfig)

    install(ContentNegotiation) {
        gson { }
    }

    val timeLive = 60000
    install(Authentication) {
        jwt("auth-jwt") {
            validate { credential ->
                val stringMap = credential.payload.getClaim("stringMap").asMap() ?: return@validate null
                val time = System.currentTimeMillis() - credential.payload.expiresAt.time
                if (time > timeLive) return@validate null
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

    configV3(rootPath, volume)

    routing {
        test()
    }

    println("version - 0.5.0-1")

}

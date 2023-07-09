package com.uogames.dictinary.v3.plugins

import com.uogames.dictinary.v3.buildPath
import com.uogames.dictinary.v3.db.entity.*
import com.uogames.dictinary.v3.defaultUUID
import com.uogames.dictinary.v3.ifNull
import com.uogames.dictinary.v3.provider.PhraseProvider
import com.uogames.dictinary.v3.toLongOrDefault
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.phrase(path: String) {

    val rootPath = environment?.rootPath ?: ""

    val service = PhraseProvider

    route("$path/phrase") {

        get {
            val text = call.parameters["text"]
            val language = call.parameters["lang"]
            val country = call.parameters["country"]
            val number = call.parameters["number"].toLongOrDefault(0)
            runCatching {
                service.get(text, language, country, number)?.let {
                    return@get call.respond(it)
                }.ifNull {
                    return@get call.respond(HttpStatusCode.BadRequest)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/view") {
            val text = call.parameters["text"]
            val language = call.parameters["lang"]
            val country = call.parameters["country"]
            val number = call.parameters["number"].toLongOrDefault(0)
            runCatching {
                service.getView(text, language, country, number)?.let {
                    it.image?.apply { imageUri = buildPath("$rootPath$path$imageUri") }
                    it.pronounce?.apply { audioUri = buildPath("$rootPath$path$audioUri") }
                    return@get call.respond(it)
                }.ifNull {
                    return@get call.respond(HttpStatusCode.BadRequest)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/test/view") {
            val text = call.parameters["text"]
            val language = call.parameters["lang"]
            val country = call.parameters["country"]
            val number = call.parameters["number"].toLongOrDefault(0)
            runCatching {
                val r = transaction {
                    CardViewEntity.all().firstOrNull()
                }
                println(r)
                return@get call.respond(HttpStatusCode.OK)
            }.onFailure {
                throw it
                return@get call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/{id}") {
            val id = call.parameters["id"].orEmpty()
            runCatching {
                service.get(UUID.fromString(id))?.let {
                    return@get call.respond(it)
                }.ifNull {
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

        get("/view/{id}") {
            val id = call.parameters["id"].orEmpty()
            runCatching {
                service.getView(UUID.fromString(id))?.let {
                    it.image?.apply { imageUri = buildPath("$rootPath$path$imageUri") }
                    it.pronounce?.apply { audioUri = buildPath("$rootPath$path$audioUri") }
                    return@get call.respond(it)
                }.ifNull {
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

        get("/count") {
            val text = call.parameters["text"]
            val language = call.parameters["lang"]
            val country = call.parameters["country"]
            runCatching {
                return@get call.respond(service.count(text, language, country))
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest)
            }
        }

        authenticate("auth-jwt") {

            post {

                val map = call.principal<JWTPrincipal>()
                    ?.payload
                    ?.getClaim("stringMap")
                    ?.asMap()
                    .ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val userName = map["Identifier"]
                    ?.toString()
                    .ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val uid = map["User UID"]
                    ?.toString()
                    .ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                runCatching {

                    val phrase = call.receiveNullable<Phrase>()
                        .ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                    val user = User(uid, userName)

                    return@post call.respond(service.update(phrase, user))
                }.onFailure {
                    println(it.message)
                    return@post call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
                }
            }

        }

    }

}
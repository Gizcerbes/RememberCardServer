package com.uogames.dictinary.v3.plugins

import com.uogames.dictinary.v3.db.dao.PhraseService
import com.uogames.dictinary.v3.db.entity.Phrase
import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.defaultUUID
import com.uogames.dictinary.v3.ifNull
import com.uogames.dictinary.v3.toLongOrDefault
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.phrase(path: String) {

    val service = PhraseService

    route("$path/phrase") {

        get {
            val text = call.parameters["text"]
            val language = call.parameters["lang"]
            val country = call.parameters["country"]
            val number = call.parameters["number"].toLongOrDefault(0)
            runCatching {
                service.get(text, language, country, number)
            }.onSuccess {
                return@get it?.let {
                    call.respond(it)
                }.ifNull {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/{id}") {
            val id = call.parameters["id"].ifNull { return@get call.respond(HttpStatusCode.BadRequest) }
            val uuid = UUID.fromString(id)
            service.get(uuid)?.let {
                return@get call.respond(it)
            }.ifNull {
                return@get call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/count") {
            val text = call.parameters["text"]
            val language = call.parameters["lang"]
            val country = call.parameters["country"]
            runCatching {
                service.count(text, language, country)
            }.onSuccess {
                return@get call.respond(it)
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest)
            }
        }

        authenticate("auth-jwt") {

            post {
                val map = call.principal<JWTPrincipal>()?.payload?.getClaim("stringMap")?.asMap().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val userName = map["Identifier"]?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val uid = map["User UID"]?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val phrase = call.receiveNullable<Phrase>().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }

                val user = User(uid, userName)

                if (phrase.globalId == defaultUUID) {
                    phrase.globalOwner = uid
                    call.respond(service.new(phrase, user))
                } else if (phrase.globalOwner == uid) {
                    service.update(phrase, user)?.let {
                        call.respond(it)
                    }.ifNull {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }

        }

    }

}
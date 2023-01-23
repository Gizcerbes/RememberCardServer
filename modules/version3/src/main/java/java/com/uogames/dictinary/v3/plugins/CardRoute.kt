package com.uogames.dictinary.v3.plugins

import com.uogames.dictinary.v3.db.dao.CardService
import com.uogames.dictinary.v3.db.entity.Card
import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.defaultUUID
import com.uogames.dictinary.v3.ifNull
import com.uogames.dictinary.v3.ifNullOrEmpty
import com.uogames.dictinary.v3.toLongOrDefault
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.card(path: String) {

    val provider = CardService

    route("$path/card") {

        get {
            val text = call.parameters["text"]
            val langFirst = call.parameters["lang-first"]
            val langSecond = call.parameters["lang-second"]
            val countryFirst = call.parameters["country-first"]
            val countrySecond = call.parameters["country-second"]
            val number = call.parameters["number"].toLongOrDefault(0)
            runCatching {
                provider.get(text, langFirst, langSecond, countryFirst, countrySecond, number)
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
            val id = call.parameters["id"].ifNullOrEmpty { return@get call.respond(HttpStatusCode.BadRequest) }
            val uuid = UUID.fromString(id)
            provider.get(uuid)?.let {
                return@get call.respond(it)
            }.ifNull {
                return@get call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/count") {
            val text = call.parameters["text"]
            val langFirst = call.parameters["lang-first"]
            val langSecond = call.parameters["lang-second"]
            val countryFirst = call.parameters["country-first"]
            val countrySecond = call.parameters["country-second"]
            runCatching {
                provider.count(text, langFirst, langSecond, countryFirst, countrySecond)
            }.onSuccess {
                return@get call.respond(it)
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
                val card = call.receiveNullable<Card>()
                    .ifNull { return@post call.respond(HttpStatusCode.BadRequest) }

                val user = User(uid, userName)

                if (card.globalId == defaultUUID) {
                    card.globalOwner = uid
                    call.respond(provider.new(card, user))
                } else if (card.globalOwner == uid) {
                    provider.update(card, user)?.let {
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
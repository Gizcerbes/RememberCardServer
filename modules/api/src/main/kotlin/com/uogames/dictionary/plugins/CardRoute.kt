package com.uogames.dictionary.plugins


import com.uogames.dictionary.db.provider.CardProvider
import com.uogames.dictionary.db.provider.dto.CardDTO
import com.uogames.dictionary.db.provider.dto.UserDTO
import com.uogames.dictionary.service.defaultUUID
import com.uogames.dictionary.service.ifNull
import com.uogames.dictionary.service.ifNullOrEmpty
import com.uogames.dictionary.service.toLong
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.card() {

    val provider = CardProvider

    route("/card") {

        get {
            val like = call.parameters["like"].ifNull { return@get call.respond(HttpStatusCode.BadRequest) }
            val number = call.parameters["number"].toLong(0)
            if (like.isEmpty()) return@get call.respond(HttpStatusCode.BadRequest)
            provider.get(like, number)?.let {
                return@get call.respond(it)
            }.ifNull {
                return@get call.respond(HttpStatusCode.NotFound)
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
            val like = call.parameters["like"].orEmpty()
            if (like.isEmpty()) return@get call.respond(HttpStatusCode.BadRequest)
            return@get call.respond(provider.count(like))
        }

        authenticate("auth-jwt") {

            post {
                val map = call.principal<JWTPrincipal>()?.payload?.getClaim("stringMap")?.asMap().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val userName = map["Identifier"]?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val uid = map["User UID"]?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val card = call.receiveNullable<CardDTO>().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }

                val user = UserDTO(uid, userName)

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
package com.uogames.dictionary.plugins

import com.uogames.dictionary.db.provider.ModuleProvider
import com.uogames.dictionary.db.provider.dto.ModuleDTO
import com.uogames.dictionary.db.provider.dto.UserDTO
import com.uogames.dictionary.service.defaultUUID
import com.uogames.dictionary.service.ifNull
import com.uogames.dictionary.service.toLong
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.module() {

    val service = ModuleProvider

    route("/module") {

        get {
            val like = call.parameters["like"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val number = call.parameters["number"].toLong(0)
            if (like.isEmpty()) return@get call.respond(HttpStatusCode.BadRequest)
            service.get(like, number)?.let {
                return@get call.respond(it)
            }.ifNull {
                return@get call.respond(HttpStatusCode.NotFound)
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
            val like = call.parameters["like"].orEmpty()
            if (like.isEmpty()) return@get call.respond(HttpStatusCode.BadRequest)
            return@get call.respond(service.count(like))
        }

        authenticate("auth-jwt") {

            post {
                val map = call.principal<JWTPrincipal>()?.payload?.getClaim("stringMap")?.asMap().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val userName = map["Identifier"]?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val uid = map["User UID"]?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val module = call.receiveNullable<ModuleDTO>().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }

                val user = UserDTO(uid, userName)

                if (module.globalId == defaultUUID) {
                    module.globalOwner = uid
                    return@post call.respond(service.new(module, user))
                } else if (module.globalOwner == uid) {
                    service.update(module, user)?.let {
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
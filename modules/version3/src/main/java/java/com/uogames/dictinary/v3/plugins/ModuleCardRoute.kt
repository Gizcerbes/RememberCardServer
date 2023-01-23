package com.uogames.dictinary.v3.plugins

import com.uogames.dictinary.v3.db.dao.ModuleCardService
import com.uogames.dictinary.v3.db.entity.ModuleCard
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
import java.util.UUID


fun Route.moduleCard(path:String) {

    val service = ModuleCardService

    route("$path/module-card") {

        get {
            val moduleID = call.parameters["moduleID"].ifNull { return@get call.respond(HttpStatusCode.BadRequest) }
            val number = call.parameters["number"].toLongOrDefault(0)
            val uuid = UUID.fromString(moduleID)
            service.get(uuid, number)?.let {
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

        get("/count/{id}") {
            val id = call.parameters["id"].ifNull { return@get call.respond(HttpStatusCode.BadRequest) }
            val uuid = UUID.fromString(id)
            return@get call.respond(service.count(uuid))
        }

        authenticate("auth-jwt") {

            post {
                val map = call.principal<JWTPrincipal>()?.payload?.getClaim("stringMap")?.asMap().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val userName = map["Identifier"]?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val uid = map["User UID"]?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val card = call.receiveNullable<ModuleCard>().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }

                val user = User(uid, userName)

                if (card.globalId == defaultUUID) {
                    card.globalOwner = uid
                    return@post call.respond(service.new(card, user))
                } else if (card.globalOwner == uid) {
                    service.update(card, User(uid, userName))?.let {
                        return@post call.respond(it)
                    }.ifNull {
                        return@post call.respond(HttpStatusCode.BadRequest)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

        }

    }

}
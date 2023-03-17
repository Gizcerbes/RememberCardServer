package com.uogames.dictinary.v3.plugins

import com.uogames.dictinary.v3.db.entity.ModuleCard
import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.defaultUUID
import com.uogames.dictinary.v3.ifNull
import com.uogames.dictinary.v3.provider.ModuleCardProvider
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

    val service = ModuleCardProvider

    route("$path/module-card") {

        get {
            val moduleID = call.parameters["module-id"].orEmpty()
            val number = call.parameters["number"].toLongOrDefault(0)
            runCatching {
                service.get(UUID.fromString(moduleID), number)?.let {
                    return@get call.respond(it)
                }.ifNull{
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

        get("/view") {
            val moduleID = call.parameters["module-id"].orEmpty()
            val number = call.parameters["number"].toLongOrDefault(0)
            runCatching {
                service.getView(UUID.fromString(moduleID), number)?.let {
                    return@get call.respond(it)
                }.ifNull{
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

        get("/{id}") {
            val id = call.parameters["id"].orEmpty()
            runCatching {
                service.get(UUID.fromString(id))?.let {
                    return@get call.respond(it)
                }.ifNull{
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
                    return@get call.respond(it)
                }.ifNull{
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

        get("/count/{id}") {
            val id = call.parameters["id"].orEmpty()
            runCatching {
                return@get call.respond(service.count(UUID.fromString(id)))
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
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
                val card = call
                    .receiveNullable<ModuleCard>()
                    .ifNull { return@post call.respond(HttpStatusCode.BadRequest) }

                val user = User(uid, userName)

                runCatching {
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
                        return@post call.respond(HttpStatusCode.BadRequest)
                    }
                }.onFailure {
                    return@post call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
                }

            }

        }

    }

}
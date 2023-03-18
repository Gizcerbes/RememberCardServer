package com.uogames.dictinary.v3.plugins

import com.uogames.dictinary.v3.db.dao.ModuleService
import com.uogames.dictinary.v3.db.entity.Module
import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.defaultUUID
import com.uogames.dictinary.v3.ifNull
import com.uogames.dictinary.v3.provider.ModuleProvider
import com.uogames.dictinary.v3.toLongOrDefault
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.module(path: String) {

    val service = ModuleProvider

    route("$path/module") {

        get {
            val text = call.parameters["text"]
            val langFirst = call.parameters["lang-first"]
            val langSecond = call.parameters["lang-second"]
            val countryFirst = call.parameters["country-first"]
            val countrySecond = call.parameters["country-second"]
            val number = call.parameters["number"].toLongOrDefault(0)
            runCatching {
                service.get(text,langFirst, langSecond, countryFirst, countrySecond, number)?.let {
                    return@get call.respond(it)
                }.ifNull {
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/view") {
            val text = call.parameters["text"]
            val langFirst = call.parameters["lang-first"]
            val langSecond = call.parameters["lang-second"]
            val countryFirst = call.parameters["country-first"]
            val countrySecond = call.parameters["country-second"]
            val number = call.parameters["number"].toLongOrDefault(0)
            runCatching {
                service.getView(text,langFirst, langSecond, countryFirst, countrySecond, number)?.let {
                    return@get call.respond(it)
                }.ifNull {
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
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
            val langFirst = call.parameters["lang-first"]
            val langSecond = call.parameters["lang-second"]
            val countryFirst = call.parameters["country-first"]
            val countrySecond = call.parameters["country-second"]
            runCatching {
                return@get call.respond(service.count(text, langFirst, langSecond, countryFirst, countrySecond))
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
                val module = call.receiveNullable<Module>()
                    .ifNull { return@post call.respond(HttpStatusCode.BadRequest) }

                val user = User(uid, userName)

                runCatching {
                    if (module.globalId == defaultUUID) {
                        module.globalOwner = uid
                        return@post call.respond(service.new(module, user))
                    } else if (module.globalOwner == uid) {
                        service.update(module, user)?.let {
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
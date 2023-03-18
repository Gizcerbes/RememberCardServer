package com.uogames.dictinary.v3.plugins

import com.uogames.dictinary.v3.*
import com.uogames.dictinary.v3.db.entity.Card
import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.provider.CardProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.card(path: String) {

    val rootPath = environment?.rootPath ?: ""
    val provider = CardProvider

    route("$path/card") {

        get {
            val text = call.parameters["text"]
            val langFirst = call.parameters["lang-first"]
            val langSecond = call.parameters["lang-second"]
            val countryFirst = call.parameters["country-first"]
            val countrySecond = call.parameters["country-second"]
            val number = call.parameters["number"].toLongOrDefault(0)
            runCatching {
                provider.get(text, langFirst, langSecond, countryFirst, countrySecond, number)?.let {
                    return@get call.respond(it)
                }.ifNull{
                    return@get call.respond(HttpStatusCode.BadRequest)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
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
                provider.getView(text, langFirst, langSecond, countryFirst, countrySecond, number)?.let {
                    it.image?.apply { imageUri = buildPath("$rootPath$path$imageUri") }
                    it.phrase.image?.apply { imageUri = buildPath("$rootPath$path$imageUri") }
                    it.phrase.pronounce?.apply { audioUri = buildPath("$rootPath$path$audioUri") }
                    it.translate.image?.apply { imageUri = buildPath("$rootPath$path$imageUri") }
                    it.translate.pronounce?.apply { audioUri = buildPath("$rootPath$path$audioUri") }
                    return@get call.respond(it)
                }.ifNull{
                    return@get call.respond(HttpStatusCode.BadRequest)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

        get("/{id}") {
            val id = call.parameters["id"].ifNullOrEmpty { return@get call.respond(HttpStatusCode.BadRequest) }
            runCatching {
                provider.get(UUID.fromString(id))?.let {
                    return@get call.respond(it)
                }.ifNull {
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

        get("/view/{id}") {
            val id = call.parameters["id"].ifNullOrEmpty { return@get call.respond(HttpStatusCode.BadRequest) }
            runCatching {
                provider.getView(UUID.fromString(id))?.let {
                    it.image?.apply { imageUri = buildPath("$rootPath$path$imageUri") }
                    it.phrase.image?.apply { imageUri = buildPath("$rootPath$path$imageUri") }
                    it.phrase.pronounce?.apply { audioUri = buildPath("$rootPath$path$audioUri") }
                    it.translate.image?.apply { imageUri = buildPath("$rootPath$path$imageUri") }
                    it.translate.pronounce?.apply { audioUri = buildPath("$rootPath$path$audioUri") }
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
                call.respond(provider.count(text, langFirst, langSecond, countryFirst, countrySecond))
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
                val card = call.receiveNullable<Card>()
                    .ifNull { return@post call.respond(HttpStatusCode.BadRequest) }

                runCatching {
                    val user = User(uid, userName)
                    if (card.globalId == defaultUUID) {
                        card.globalOwner = uid
                        return@post call.respond(provider.new(card, user))
                    } else if (card.globalOwner == uid) {
                        provider.update(card, user)?.let {
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
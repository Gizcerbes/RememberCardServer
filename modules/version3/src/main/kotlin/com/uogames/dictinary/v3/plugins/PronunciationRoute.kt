package com.uogames.dictinary.v3.plugins

import com.uogames.dictinary.v3.db.dao.PronunciationService
import com.uogames.dictinary.v3.db.entity.Pronunciation
import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.ifNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*


fun Route.pronunciation(path:String) {

    val rootPath = environment?.rootPath ?: ""
    val dir = environment?.config?.property("ktor.rootDir")?.getString() + "/pronounce"
    val format = ".mp4"
    val service = PronunciationService
    File(dir).mkdirs()

    route("$path/pronunciation") {


        get("/{name}") {
            val filename = call.parameters["name"].ifNull { return@get call.respond(HttpStatusCode.NotFound) }
            val file = File(dir, "/$filename")

            runCatching {
                if (file.exists()) {
                    return@get call.respondFile(file)
                } else {
                    if (filename.endsWith(format, ignoreCase = true)) {
                        service.delete(UUID.fromString(filename.replace(format, "")))
                    }
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

        head("/info/{id}") {
            val filename = call.parameters["id"].orEmpty()
            runCatching {
                val file = File(dir, "/$filename$format")
                if (file.exists()){
                    return@head call.respond(HttpStatusCode.OK)
                } else {
                    service.delete(UUID.fromString(filename.replace(format, "")))
                    return@head call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@head call.respond(HttpStatusCode.BadRequest)
            }

        }

        get("/info/{id}") {
            val id = call.parameters["id"].orEmpty()
            runCatching {
                service.get(UUID.fromString(id))?.let {
                    val protocol = call.request.local.scheme
                    val host = call.request.local.serverHost
                    val port = call.request.local.serverPort
                    it.audioUri = "$protocol://$host:$port$rootPath$path${it.audioUri}"
                    return@get call.respond(it)
                }.ifNull {
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

        get("/load/{id}") {
            val id = call.parameters["id"].orEmpty()
            println(id)
            runCatching {
                service.get(UUID.fromString(id))?.let {
                    val protocol = call.request.local.scheme
                    val host = call.request.local.serverHost
                    val port = call.request.local.serverPort
                    it.audioUri = "$protocol://$host:$port$rootPath$path${it.audioUri}"
                    return@get call.respondRedirect(it.audioUri)
                }.ifNull {
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }



        authenticate("auth-jwt") {

            post("/upload") {
                val principal = call.principal<JWTPrincipal>()
                val map = principal?.payload?.getClaim("stringMap")
                    ?.asMap().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val userName = map["Identifier"]
                    ?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val uid = map["User UID"]
                    ?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val contentLength = call.request.header("content-length")
                    ?.toInt().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val contentType = call.request.header("content-type")
                    .ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                if (contentLength == 0 || contentType != "audio/mp4") return@post call.respond(HttpStatusCode.BadRequest)

                runCatching {
                    var pronounce = Pronunciation(UUID.randomUUID(), uid, "")
                    val user = User(uid, userName)
                    pronounce = service.new(pronounce, user)
                    val name = "${pronounce.globalId}$format"
                    val out = File(dir, "/$name").outputStream().buffered()
                    call.receiveStream().buffered().use { it.copyTo(out) }
                    out.close()
                    pronounce.audioUri = "/pronunciation/$name"
                    service.update(pronounce, user)?.let {
                        val protocol = call.request.local.scheme
                        val host = call.request.local.serverHost
                        val port = call.request.local.serverPort
                        it.audioUri = "$protocol://$host:$port$rootPath$path${it.audioUri}"
                        return@post call.respond(it)
                    }.ifNull {
                        return@post call.respond(HttpStatusCode.BadRequest)
                    }
                }.onFailure {
                    return@post call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
                }
            }

        }

    }

}
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
    val service = PronunciationService
    File(dir).mkdirs()

    route("$path/pronunciation") {


        get("/{name}") {
            val filename = call.parameters["name"].ifNull { return@get call.respond(HttpStatusCode.NotFound) }
            val file = File(dir, "/$filename")
            if (file.exists()) {
                call.respondFile(file)
            } else {
                service.delete(UUID.fromString(filename.replace(".mp4", "")))
                call.respond(HttpStatusCode.NotFound)
            }
        }

        head("/info/{id}") {
            val filename = call.parameters["id"].ifNull { return@head call.respond(HttpStatusCode.NotFound) }
            val file = File(dir, "/$filename.mp4")
            if (file.exists()){
                call.respond(HttpStatusCode.OK)
            } else {
                service.delete(UUID.fromString(filename.replace(".mp4", "")))
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/info/{id}") {
            val id = call.parameters["id"].ifNull { return@get call.respond(HttpStatusCode.BadRequest) }
            val uuid = UUID.fromString(id)
            service.get(uuid)?.let {
                val protocol = call.request.local.scheme
                val host = call.request.local.host
                val port = call.request.local.port
                it.audioUri = "$protocol://$host:$port$rootPath${it.audioUri}"
                call.respond(it)
            }.ifNull {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/load/{id}") {
            val id = call.parameters["id"].ifNull { return@get call.respond(HttpStatusCode.BadRequest) }
            val uuid = UUID.fromString(id)
            service.get(uuid)?.let {
                val protocol = call.request.local.scheme
                val host = call.request.local.host
                val port = call.request.local.port
                it.audioUri = "$protocol://$host:$port$rootPath${it.audioUri}"
                call.respondRedirect(it.audioUri)
            }.ifNull {
                call.respond(HttpStatusCode.BadRequest)
            }
        }



        authenticate("auth-jwt") {

            post("/upload") {
                val principal = call.principal<JWTPrincipal>()
                val map = principal?.payload?.getClaim("stringMap")?.asMap().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val userName = map["Identifier"]?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val uid = map["User UID"]?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val contentLength = call.request.header("content-length")?.toInt().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val contentType = call.request.header("content-type") ?: return@post call.respond(HttpStatusCode.BadRequest)
                if (contentLength == 0 || contentType != "audio/mp4") return@post call.respond(HttpStatusCode.BadRequest)

                var pronounce = Pronunciation(UUID.randomUUID(), uid, "")
                val user = User(uid, userName)
                pronounce = service.new(pronounce, user)
                val name = "${pronounce.globalId}.mp4"
                val file = File(dir, "/$name")
                call.receiveStream().use { its ->
                    file.outputStream().buffered().use {
                        its.copyTo(it)
                    }
                }

                pronounce.audioUri = "/pronunciation/$name"
                service.update(pronounce, user)?.let {
                    val protocol = call.request.local.scheme
                    val host = call.request.local.host
                    val port = call.request.local.port
                    it.audioUri = "$protocol://$host:$port$rootPath${it.audioUri}"
                    call.respond(it)
                }.ifNull {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }

        }

    }

}
package com.uogames.dictionary.plugins

import com.uogames.dictionary.db.provider.ImageProvider
import com.uogames.dictionary.db.provider.dto.ImageDTO
import com.uogames.dictionary.db.provider.dto.UserDTO
import com.uogames.dictionary.service.ifNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.UUID


fun Route.image() {

    val rootPath = environment?.rootPath ?: ""
    val path = environment?.config?.property("ktor.rootDir")?.getString() + "/images"
    val service = ImageProvider
    File(path).mkdirs()

    route("/image") {

        get("/{name}") {
            val filename = call.parameters["name"].ifNull { return@get call.respond(HttpStatusCode.NotFound) }
            val file = File(path, "/$filename")
            if (file.exists()) {
                call.respondFile(file)
            } else {
                service.delete(UUID.fromString(filename.replace(".png", "")))
                call.respond(HttpStatusCode.NotFound)
            }
        }

        head("/info/{id}") {
            val filename = call.parameters["id"].ifNull { return@head call.respond(HttpStatusCode.NotFound) }
            val file = File(path, "/$filename.png")
            if (file.exists()){
                call.respond(HttpStatusCode.OK)
            } else {
                service.delete(UUID.fromString(filename.replace(".png", "")))
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
                it.imageUri = "$protocol://$host:$port$rootPath${it.imageUri}"
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
                it.imageUri = "$protocol://$host:$port$rootPath${it.imageUri}"
                call.respondRedirect(it.imageUri)
            }.ifNull {
                call.respond(HttpStatusCode.BadRequest)
            }

        }

        authenticate("auth-jwt") {

            post("upload") {
                val principal = call.principal<JWTPrincipal>()
                val map = principal?.payload?.getClaim("stringMap")?.asMap().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val userName = map["Identifier"]?.toString().ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val uid = map["User UID"]?.toString() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val contentLength = call.request.header("content-length")?.toInt() ?: return@post call.respond(
                    HttpStatusCode.BadRequest
                )
                val contentType = call.request.header("content-type") ?: return@post call.respond(HttpStatusCode.BadRequest)
                if (contentLength == 0 || contentType != "image/png") return@post call.respond(HttpStatusCode.BadRequest)

                var image = ImageDTO(UUID.randomUUID(), uid, "")
                val user = UserDTO(uid, userName)
                image = service.new(image, user)
                val name = "${image.globalId}.png"
                val file = File(path, "/$name")
                call.receiveStream().use { its ->
                    file.outputStream().buffered().use {
                        its.copyTo(it)
                    }
                }
                image.imageUri = "/image/$name"
                service.update(image, user)?.let {
                    val protocol = call.request.local.scheme
                    val host = call.request.local.host
                    val port = call.request.local.port
                    it.imageUri = "$protocol://$host:$port$rootPath${it.imageUri}"
                    call.respond(it)
                }.ifNull {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }

        }


    }

}
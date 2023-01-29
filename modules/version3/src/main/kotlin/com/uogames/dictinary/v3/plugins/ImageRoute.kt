package com.uogames.dictinary.v3.plugins

import com.uogames.dictinary.v3.db.dao.ImageService
import com.uogames.dictinary.v3.db.entity.Image
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
import java.util.UUID


fun Route.image(path: String) {

    val rootPath = environment?.rootPath ?: ""
    val dir = environment?.config?.property("ktor.rootDir")?.getString() + "/images"
    val format = ".png"
    val service = ImageService
    File(dir).mkdirs()

    route("$path/image") {

        get("/{name}") {
            val filename = call.parameters["name"].ifNull { return@get call.respond(HttpStatusCode.NotFound) }
            val file = File(dir, "/$filename")
            runCatching {
                if (file.exists()) {
                    call.respondFile(file)
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
            val file = File(dir, "/$filename$format")
            runCatching {
                if (file.exists()) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    service.delete(UUID.fromString(filename.replace(format, "")))
                    call.respond(HttpStatusCode.NotFound)
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
                    it.imageUri = "$protocol://$host:$port$rootPath${it.imageUri}"
                    return@get call.respond(it)
                }.ifNull {
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(status = HttpStatusCode.BadRequest, message = it.message.toString())
            }
        }

        get("/load/{id}") {
            val id = call.parameters["id"].ifNull { return@get call.respond(HttpStatusCode.BadRequest) }
            runCatching {
                service.get(UUID.fromString(id))?.let {
                    val protocol = call.request.local.scheme
                    val host = call.request.local.serverHost
                    val port = call.request.local.serverPort
                    it.imageUri = "$protocol://$host:$port$rootPath${it.imageUri}"
                    call.respondRedirect(it.imageUri)
                }.ifNull {
                    call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

        authenticate("auth-jwt") {

            post("upload") {
                val principal = call.principal<JWTPrincipal>()
                val map = principal?.payload?.getClaim("stringMap")?.asMap().ifNull {
                    return@post call.respond(HttpStatusCode.BadRequest)
                }
                val userName = map["Identifier"]?.toString().ifNull {
                    return@post call.respond(HttpStatusCode.BadRequest)
                }
                val uid = map["User UID"]?.toString().ifNull {
                    return@post call.respond(HttpStatusCode.BadRequest)
                }
                val contentLength = call.request.header("content-length")?.toInt().ifNull {
                    return@post call.respond(HttpStatusCode.BadRequest)
                }
                val contentType = call.request.header("content-type").ifNull {
                    return@post call.respond(HttpStatusCode.BadRequest)
                }
                if (contentLength == 0 || contentType != "image/png") return@post call.respond(HttpStatusCode.BadRequest)

                runCatching {
                    var image = Image(UUID.randomUUID(), uid, "")
                    val user = User(uid, userName)
                    image = service.new(image, user)
                    val name = "${image.globalId}$format"
                    val os = File(dir, "/$name").outputStream().buffered()
                    call.receiveStream().use { it.copyTo(os) }
                    image.imageUri = "/image/$name"
                    service.update(image, user)?.let {
                        val protocol = call.request.local.scheme
                        val host = call.request.local.serverHost
                        val port = call.request.local.serverPort
                        it.imageUri = "$protocol://$host:$port$rootPath${it.imageUri}"
                        return@post call.respond(it)
                    }.ifNull {
                        return@post call.respond(HttpStatusCode.BadRequest)
                    }
                }.onFailure {
                    return@post call.respond(HttpStatusCode.BadRequest)
                }

            }

        }


    }

}
package com.uogames.dictinary.v3.plugins

import com.google.gson.Gson
import com.uogames.dictinary.v3.Exchanges.printBody
import com.uogames.dictinary.v3.Exchanges.printHeader
import com.uogames.dictinary.v3.buildPath
import com.uogames.dictinary.v3.db.entity.Image
import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.ifNull
import com.uogames.dictinary.v3.provider.ImageProvider
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.UUID


fun Route.image(path: String, rootPath: String, volume: String) {

    //val rootPath = environment?.rootPath ?: ""
    //val dir = environment?.config?.property("ktor.rootDir")?.getString() + "/images"
    val dir = "$volume/images"
    val format = ".png"
    val service = ImageProvider
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
                    it.apply { imageUri = buildPath("$rootPath$path$imageUri") }
                    return@get call.respond(it)
                }.ifNull {
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(status = HttpStatusCode.BadRequest, message = it.message.toString())
            }
        }

        get("/info/view/{id}") {
            val id = call.parameters["id"].orEmpty()
            runCatching {
                service.getView(UUID.fromString(id))?.let {
                    it.apply { imageUri = buildPath("$rootPath$path$imageUri") }
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
                    it.apply { imageUri = buildPath("$rootPath$path$imageUri") }
                    call.respondRedirect(it.imageUri)
                }.ifNull {
                    call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

        authenticate("auth-jwt") {

            post("/upload") {
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
                    os.close()
                    image.imageUri = "/image/$name"
                    service.update(image, user)?.let {
                        it.apply { imageUri = buildPath("$rootPath$path$imageUri") }
                        return@post call.respond(it)
                    }.ifNull {
                        return@post call.respond(HttpStatusCode.BadRequest)
                    }
                }.onFailure {
                    return@post call.respond(HttpStatusCode.BadRequest)
                }
            }

            post("/upload/v2") {
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
                runCatching {
                    var image: Image? = null
                    var name = ""
                    val multipartData = call.receiveMultipart()
                    multipartData.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                image = Gson().fromJson(part.value, Image::class.java)
                                name = "${image!!.globalId}$format"
                                image!!.imageUri = "/image/$name"
                                service.update(image!!, User(uid, userName))
                            }

                            is PartData.FileItem -> {
                                val fileBytes = part.streamProvider().readBytes()
                                File(dir, "/$name").writeBytes(fileBytes)
                            }

                            else -> {}
                        }
                        part.dispose()
                    }
                    image?.let { call.respond(it) } ?: call.respond(HttpStatusCode.BadRequest)
                }.onFailure {
                    println(it.message)
                }
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
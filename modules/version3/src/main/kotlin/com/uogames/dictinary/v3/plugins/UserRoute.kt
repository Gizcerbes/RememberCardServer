package com.uogames.dictinary.v3.plugins

import com.uogames.dictinary.v3.db.dao.UserService
import com.uogames.dictinary.v3.ifNull
import com.uogames.dictinary.v3.provider.UserProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.users(path:String){

    val provider = UserProvider

    route("$path/user"){

        get("/info/{id}") {
            val id = call.parameters["id"].orEmpty()
            runCatching {
                provider.get(id)?.let {
                   return@get call.respond(it)
                }.ifNull {
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

        get("/info/view/{id}") {
            val id = call.parameters["id"].orEmpty()
            runCatching {
                provider.get(id)?.let {
                    return@get call.respond(it)
                }.ifNull{
                    return@get call.respond(HttpStatusCode.NotFound)
                }
            }.onFailure {
                return@get call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
            }
        }

    }

}
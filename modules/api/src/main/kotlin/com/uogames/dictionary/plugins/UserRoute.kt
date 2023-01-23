package com.uogames.dictionary.plugins

import com.uogames.dictionary.db.entity.v2.dao.UserService
import com.uogames.dictionary.db.provider.UserProvider
import com.uogames.dictionary.service.ifNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.users(){

    route("/user"){

        get("/info/{id}") {
            val id = call.parameters["id"].ifNull { return@get call.respond(HttpStatusCode.BadRequest) }
            UserProvider.get(id)?.let {
                call.respond(it)
            }.ifNull {
                call.respond(HttpStatusCode.NotFound)
            }
        }


    }

}
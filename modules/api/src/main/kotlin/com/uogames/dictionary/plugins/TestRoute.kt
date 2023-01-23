package com.uogames.dictionary.plugins

import com.uogames.dictionary.db.entity.v2.dao.ModuleService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

data class StatusDTO(
    val timestamp: Long = Date().time,
    val type: HttpStatusCode = HttpStatusCode.OK,
    val message: String = type.description
)

fun Route.test() {

    route("/test") {


        get {
            val text = call.parameters["text"]
            val langFirst = call.parameters["langFirst"]
            val langSecond = call.parameters["langSecond"]
            val countryFirst: String? = call.parameters["countryFirst"]
            val countrySecond = call.parameters["countrySecond"]
            val number = call.parameters["number"]?.toLongOrNull() ?: 0
            val res = 1
            if (res == null) call.respond(HttpStatusCode.BadRequest)
            else call.respond(res)
        }

    }

}

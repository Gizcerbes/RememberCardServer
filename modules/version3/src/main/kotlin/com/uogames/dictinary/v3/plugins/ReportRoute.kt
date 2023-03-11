package com.uogames.dictinary.v3.plugins

import com.uogames.dictinary.v3.db.dao.ReportService
import com.uogames.dictinary.v3.db.dao.UserService
import com.uogames.dictinary.v3.db.entity.Report
import com.uogames.dictinary.v3.db.entity.User
import com.uogames.dictinary.v3.ifNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.report(path:String){

    route("$path/report"){

        val service = ReportService

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
                val report = call.receiveNullable<Report>()
                    .ifNull { return@post call.respond(HttpStatusCode.BadRequest) }
                val user = User(uid, userName)
                runCatching {
                    service.register(user,report)
                    return@post call.respond(HttpStatusCode.OK)
                }.onFailure {
                    return@post call.respond(HttpStatusCode.BadRequest, message = it.message.orEmpty())
                }

            }

        }

    }

}
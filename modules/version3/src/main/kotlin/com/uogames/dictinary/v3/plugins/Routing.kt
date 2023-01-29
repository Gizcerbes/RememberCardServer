package com.uogames.dictinary.v3.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configV3(){

    val path = "/v3"

    routing {
        card(path)
        image(path)
        moduleCard(path)
        module(path)
        phrase(path)
        pronunciation(path)
        users(path)
    }


}
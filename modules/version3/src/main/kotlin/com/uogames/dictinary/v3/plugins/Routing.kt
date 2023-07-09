package com.uogames.dictinary.v3.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configV3(rootPath: String, volume: String){

    val path = "/v3"

    routing {
        card(path)
        image(path, rootPath, volume)
        moduleCard(path)
        module(path)
        phrase(path)
        pronunciation(path, rootPath, volume)
        users(path)
        report(path)
    }


}
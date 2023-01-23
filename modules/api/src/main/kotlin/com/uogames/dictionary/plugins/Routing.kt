package com.uogames.dictionary.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {

    routing {
        test()
        image()
        phrase()
        pronunciation()
        module()
        moduleCard()
        card()
    }

}

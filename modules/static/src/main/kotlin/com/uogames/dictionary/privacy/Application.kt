package com.uogames.dictionary.privacy

import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.ktor.server.tomcat.*


fun main(){
    embeddedServer(Tomcat, port = 8082){
        routing {
            static {
                defaultResource("app/policy.html")
            }
        }
    }.start(true)
}

//fun main(args: Array<String>): Unit = io.ktor.server.tomcat.EngineMain.main(args)
//
//fun Application.module(testing: Boolean = false){
//
//
//
//}
package com.uogames.clientApi.version2.network.service

import com.uogames.clientApi.version2.network.ifSuccess
import com.uogames.clientApi.version2.network.response.ModuleResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class ModuleService(private val client: HttpClient) {

    suspend fun get(like: String, number: Long): ModuleResponse = client
        .get("/remember-card/module") {
            parameter("like", like)
            parameter("number", number)
        }.ifSuccess()

    suspend fun count(like: String): Long = client
        .get("/remember-card/module/count") {
            parameter("like", like)
        }.ifSuccess()

    suspend fun get(globalId: UUID): ModuleResponse = client
        .get("/remember-card/module/$globalId")
        .ifSuccess()

    suspend fun post(module: ModuleResponse): ModuleResponse = client
        .post("/remember-card/module") {
            contentType(ContentType.Application.Json)
            setBody(module)
        }.ifSuccess()

}
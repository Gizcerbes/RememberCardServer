package com.uogames.clientApi.version2.network.service

import com.uogames.clientApi.version2.network.ifSuccess
import com.uogames.clientApi.version2.network.response.ModuleCardResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class ModuleCardService(private val client: HttpClient) {

    suspend fun count(moduleID: UUID): Long = client
        .get("/remember-card/module-card/count/$moduleID")
        .ifSuccess()

    suspend fun get(moduleID: UUID, number: Long): ModuleCardResponse = client
        .get("/remember-card/module-card") {
            parameter("moduleID", moduleID)
            parameter("number", number)
        }.ifSuccess()

    suspend fun get(globalId: UUID): ModuleCardResponse = client
        .get("/remember-card/module-card/$globalId")
        .ifSuccess()

    suspend fun post(moduleCard: ModuleCardResponse): ModuleCardResponse = client
        .post("/remember-card/module-card") {
            contentType(ContentType.Application.Json)
            setBody(moduleCard)
        }.ifSuccess()


}
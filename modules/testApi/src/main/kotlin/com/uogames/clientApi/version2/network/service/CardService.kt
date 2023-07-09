package com.uogames.clientApi.version2.network.service

import com.uogames.clientApi.version2.network.ifSuccess
import com.uogames.clientApi.version2.network.response.CardResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class CardService(private val client: HttpClient) {

    suspend fun get(like: String, number: Long): CardResponse = client
        .get("/remember-card/card") {
            parameter("like", like)
            parameter("number", number)
        }.ifSuccess()

    suspend fun get(globalId: UUID): CardResponse = client
        .get("/remember-card/card/$globalId")
        .ifSuccess()

    suspend fun count(like: String): Long = client
        .get("/remember-card/card/count") {
            parameter("like", like)
        }.ifSuccess()

    suspend fun post(card: CardResponse): CardResponse = client
        .post("/remember-card/card") {
            contentType(ContentType.Application.Json)
            setBody(card)
        }.ifSuccess()

}
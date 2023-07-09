package com.uogames.clientApi.version2.network.service

import com.uogames.clientApi.version2.network.ifSuccess
import com.uogames.clientApi.version2.network.response.PhraseResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class PhraseService(private val client: HttpClient) {

    suspend fun count(like: String): Long = client
        .get("/remember-card/phrase/count") {
            parameter("like", like)
        }.ifSuccess()

    suspend fun get(like: String, number: Long): PhraseResponse = client
        .get("/remember-card/phrase") {
            parameter("like", like)
            parameter("number", number)
        }.ifSuccess()

    suspend fun get(globalId: UUID): PhraseResponse = client
        .get("/remember-card/phrase/$globalId")
        .ifSuccess()

    suspend fun post(phrase: PhraseResponse): PhraseResponse = client
        .post("/remember-card/phrase") {
            contentType(ContentType.Application.Json)
            setBody(phrase)
        }.ifSuccess()

}
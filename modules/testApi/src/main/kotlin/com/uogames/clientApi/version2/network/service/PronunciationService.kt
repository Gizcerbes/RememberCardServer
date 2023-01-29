package com.uogames.clientApi.version2.network.service

import com.google.gson.Gson
import com.uogames.clientApi.version2.network.response.PronunciationResponse
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import com.uogames.clientApi.version2.network.ifSuccess
import io.ktor.client.*
import io.ktor.client.statement.*
import java.util.*

class PronunciationService(private val client: HttpClient) {

    suspend fun get(globalId: UUID): PronunciationResponse = client
        .get("/remember-card/pronunciation/info/$globalId")
        .ifSuccess()

    suspend fun load(globalId: UUID): ByteArray = client
        .get("/remember-card/pronunciation/load/$globalId")
        .ifSuccess()

    suspend fun upload(
        byteArray: ByteArray,
        onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ): PronunciationResponse = client
        .post("/remember-card/pronunciation/upload") {
            contentType(ContentType.Audio.MP4)
            setBody(byteArray)
            onUpload(onUpload)
        }.ifSuccess()

    suspend fun exists(globalId: UUID): Boolean = client
        .head("/remember-card/pronunciation/info/$globalId").status == HttpStatusCode.OK
}
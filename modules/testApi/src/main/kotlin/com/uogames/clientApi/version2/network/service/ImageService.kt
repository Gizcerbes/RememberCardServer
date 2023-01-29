package com.uogames.clientApi.version2.network.service

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import com.uogames.clientApi.version2.network.ifSuccess
import com.uogames.clientApi.version2.network.response.ImageResponse
import io.ktor.client.*
import java.util.*

class ImageService(private val client: HttpClient) {

    suspend fun get(globalId: UUID): ImageResponse = client
        .get("/remember-card/image/info/$globalId")
        .ifSuccess()

    suspend fun load(globalId: UUID): ByteArray = client
        .get("/remember-card/image/load/$globalId")
        .ifSuccess()

    suspend fun upload(
        byteArray: ByteArray,
        onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ): ImageResponse = client
        .post("/remember-card/image/upload") {
            contentType(ContentType.Image.PNG)
            setBody(byteArray)
            onUpload(onUpload)
        }.ifSuccess()

    suspend fun exists(globalId: UUID): Boolean = client
        .head("/remember-card/image/info/$globalId").status == HttpStatusCode.OK

}
package com.uogames.clientApi.version2.network.service

import com.uogames.clientApi.version2.network.ifSuccess
import com.uogames.clientApi.version2.network.response.UserResponse
import io.ktor.client.*
import io.ktor.client.request.*

class UserService(private val client: HttpClient) {

	suspend fun get(globalOwner: String): UserResponse = client
		.get("/remember-card/user/info/$globalOwner")
		.ifSuccess()

}
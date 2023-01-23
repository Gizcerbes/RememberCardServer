package com.uogames.dictionary.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.util.*
import java.util.*
import kotlin.experimental.xor

object JWTBuilder {

	fun create(secret: String, data: Map<String, String>, time: Int = 6000): String {
		var byteCode: Byte = 0
		return JWT.create().apply {
			data.forEach { (t, u) ->
				u.toByteArray().forEach { b -> byteCode = byteCode.xor(b) }
			}
			withExpiresAt(Date(System.currentTimeMillis() + time))
			withClaim("stringMap", data)
		}.sign(Algorithm.HMAC256("$secret$byteCode".encodeBase64()))
	}

	fun check(secret: String, token: String): JWTVerifier? {
		val decoded = JWT.decode(token)
		var byteCode: Byte = 0
		val data = decoded.getClaim("stringMap").asMap()
		data.forEach {
			it.value.toString().toByteArray().forEach { b -> byteCode = byteCode.xor(b) }
		}
		return JWT.require(Algorithm.HMAC256("$secret$byteCode".encodeBase64())).build()
	}


}
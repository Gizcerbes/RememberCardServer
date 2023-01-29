package com.uogames.clientApi.version3.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class Client(
    private val secret: (() -> String)?,
    private val data: (() -> Map<String, String>)?,
    private val defaultUrl: String,
    keystoreInput: ByteArray? = null,
    keystorePassword: CharArray? = null
) {

    private val ssl = getSsl(keystoreInput, keystorePassword)
    //private var picasso: Picasso? = null

    private val okHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(10, TimeUnit.SECONDS)
        followRedirects(false)
        connectionPool(ConnectionPool(16, 5, TimeUnit.SECONDS))
        if (ssl != null) {
            setSsl(ssl)
        }
    }.build()

    val client = HttpClient(OkHttp) {
        engine {
            preconfigured = okHttpClient
            threadsCount = 16
        }
        install(DefaultRequest) {
            if (secret != null && data != null) {
                val secret = secret
                val data = data
                bearerAuth(JWTBuilder.create(secret(), data(), 10000))
            }
            url(defaultUrl)
        }
        install(ContentNegotiation) {
            gson()
        }

//		if (BuildConfig.DEBUG) install(Logging) {
//			logger = Logger.SIMPLE
//			level = LogLevel.ALL
//		}
    }

    private fun getSsl(keystoreInput: ByteArray?, keystorePassword: CharArray?): SslSettings? {
        return if (keystoreInput != null && keystorePassword != null) {
            SslSettings(keystoreInput, keystorePassword)
        } else {
            null
        }
    }

    private fun OkHttpClient.Builder.setSsl(ssl: SslSettings?) {
        if (ssl != null) {
            sslSocketFactory(ssl.getSslContext().socketFactory, ssl.getTrustManager())
            hostnameVerifier { _, _ -> true }
        }
    }

//	fun getPicasso(context: Context): Picasso {
//		if (picasso == null) synchronized(this) {
//			if (picasso == null){
//				picasso = Picasso
//					.Builder(context)
//					.downloader(OkHttp3Downloader(okHttpClient))
//					.build()
//			}
//		}
//		return picasso as Picasso
//	}

}
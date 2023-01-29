package com.uogames.client.test

import com.uogames.clientApi.version2.network.Client
import com.uogames.clientApi.version2.network.service.PhraseService
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue


class TestV2 {

    //private val uid = "123"
    private val input = TestV2::class.java.getResource("/keystore.jks")?.readBytes()

    private val client = Client(
        secret = null,
        data = null,
        defaultUrl =  "https://192.169.0.100:8081",
        keystoreInput = input,
        keystorePassword = "itismyfirstseriousapp".toCharArray()
    )

    private val phrase = PhraseService(client.client)

    @Test
    fun testPhraseCount() = runBlocking {
        val count = phrase.count("let")
        assertTrue { count >= 0L }
    }

    @Test
    fun testPhraseGet() = runBlocking {
        try {
            val p = phrase.get("let", 0)
            phrase.get(p.globalId)
            assertTrue { true }
        }catch (e:Exception){
            assertTrue { false }
        }
    }

    @After
    fun after() = runBlocking {
        client.client.close()
    }

}
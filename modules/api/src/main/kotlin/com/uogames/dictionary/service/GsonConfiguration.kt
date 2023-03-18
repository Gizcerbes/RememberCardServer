package com.uogames.dictionary.service

import com.google.gson.GsonBuilder
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.gson.*

fun Configuration.gsonWithoutExposeAnnotation(
    contentType: ContentType = ContentType.Application.Json,
    block: GsonBuilder.() -> Unit = {}
) {
    val builder = GsonBuilder().excludeFieldsWithoutExposeAnnotation()
    builder.apply(block)
    val converter = GsonConverter(builder.create())
    register(contentType, converter)
}


package com.uogames.dictionary


import com.google.gson.GsonBuilder
import com.uogames.dictinary.v3.db.entity.Phrase
import java.util.*

fun main() {
    val phrase = Phrase(UUID.randomUUID())

    val builder = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    println(builder.toJson(phrase))
}
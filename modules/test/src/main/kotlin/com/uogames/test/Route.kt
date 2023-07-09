package com.uogames.test

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

fun Route.test() {

    route("/test2") {

        get {
            println("get")
            val r = transaction { StoreTableEntity.all().map { it.toStore() } }
            return@get call.respond(r)
        }

        post {
            println("post")
            transaction {
                val itemA = ItemTableEntity.new {
                    name = "ItemA"
                    info = "InfoItemA"
                }
                val itemB = ItemTableEntity.new {
                    name = "ItemB"
                    info = "InfoItemB"
                }
                val store1 = StoreTableEntity.new {
                    storeName = "Store1"
                    region = "Texas"
                }
                val store2 = StoreTableEntity.new {
                    storeName = "Store2"
                    region = "Nevada"
                }
                val store3 = StoreTableEntity.new {
                    storeName = "Store3"
                    region = "Florida"
                }
                val store4 = StoreTableEntity.new {
                    storeName = "Store4"
                    region = "Nevada"
                }
                val store5 = StoreTableEntity.new {
                    storeName = "Store5"
                    region = "Arizona"
                }

                for (i in 0 until 10) {
                    SellTableEntity.new {
                        item = itemA
                        store = store1
                        date = LocalDate.now()
                    }
                }
                for (i in 0 until 14) {
                    SellTableEntity.new {
                        item = itemA
                        store = store2
                        date = LocalDate.now()
                    }
                }
                for (i in 0 until 8) {
                    SellTableEntity.new {
                        item = itemA
                        store = store3
                        date = LocalDate.now()
                    }
                }
                for (i in 0 until 5) {
                    SellTableEntity.new {
                        item = itemB
                        store = store4
                        date = LocalDate.now()
                    }
                }
                for (i in 0 until 12) {
                    SellTableEntity.new {
                        item = itemB
                        store = store5
                        date = LocalDate.now()
                    }
                }
                for (i in 0 until 6) {
                    SellTableEntity.new {
                        item = itemB
                        store = store1
                        date = LocalDate.now()
                    }
                }
                commit()
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}

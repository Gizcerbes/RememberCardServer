package com.uogames.test

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date
import java.util.UUID

//////////////////////////////////////////////
object ItemTable : UUIDTable(name = "item_table") {
    val name = varchar("item_name", 50)
    val info = text("item_describe")
}

class ItemTableEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ItemTableEntity>(ItemTable)

    var name by ItemTable.name
    var info by ItemTable.info

    fun toItem() = Item(id.value, name, info)
}

data class Item(
    val id: UUID,
    val name: String,
    val info: String
)

////////////////////////////////////////////////////
object StoreTable : UUIDTable(name = "store_table") {
    val storeName = varchar("store_name", 50).uniqueIndex()
    val region = varchar("region", 50)
}

class StoreTableEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<StoreTableEntity>(StoreTable)

    var storeName by StoreTable.storeName
    var region by StoreTable.region
    val sell by SellTableEntity referrersOn SellTable.store

    fun toStore() = Store(id.value, storeName, region, sell.map { it.toSell() })
}

data class Store(
    val id: UUID,
    val stareName: String,
    val region: String,
    val item: List<Sell>
)
////////////////////////////////////////////////////////////////

object SellTable : UUIDTable(name = "sell_table") {
    val item = reference("item", ItemTable.id)
    val store = reference("store", StoreTable.id)
    val date = date("date")
}

class SellTableEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SellTableEntity>(SellTable)

    var item by ItemTableEntity referencedOn SellTable.item
    var store by StoreTableEntity referencedOn SellTable.store
    var date by SellTable.date

    fun toSell() = Sell(id.value, item.toItem(), date.toString())

}

data class Sell(
    val id: UUID,
    val item: Item,
    val date: String
)

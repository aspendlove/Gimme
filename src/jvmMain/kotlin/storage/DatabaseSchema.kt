package storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object Clients: IntIdTable() {
    val businessName = varchar("businessName", 64)
    val contactName = varchar("contactName", 64)
    val street = varchar("street", 256)
    val city = varchar("city", 64)
    val state = varchar("state", 32)
    val zip = integer("zip")
    val email = varchar("email", 64)
    val phone = varchar("phone", 32)
}

class Client(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Client>(Clients)

    var businessName by Clients.businessName
    var contactName by Clients.contactName
    var street by Clients.street
    var city by Clients.city
    var state by Clients.state
    var zip by Clients.zip
    var email by Clients.email
    var phone by Clients.phone

    override fun toString(): String {
        return businessName
    }
}

object Users: IntIdTable() {
    val businessName = varchar("businessName", 64)
    val contactName = varchar("contactName", 64)
    val subtitle = varchar("subtitle", 4096)
    val street = varchar("street", 256)
    val city = varchar("city", 64)
    val state = varchar("state", 32)
    val zip = integer("zip")
    val email = varchar("email", 64)
    val phone = varchar("phone", 32)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var businessName by Users.businessName
    var contactName by Users.contactName
    var subtitle by Users.subtitle
    var street by Users.street
    var city by Users.city
    var state by Users.state
    var zip by Users.zip
    var email by Users.email
    var phone by Users.phone

    override fun toString(): String {
        return businessName
    }
}

object Items: IntIdTable() {
    val name = varchar("name", 64)
    val description = varchar("description", 4096)
    val quantity = double("quantity")
    //    val price = compositeMoney(19,4,"price", "usd")
    val price = double("price")
}

class Item(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Item>(Items)

    var name by Items.name
    var description by Items.description
    var quantity by Items.quantity
    var price by Items.price
}

object Invoices: IntIdTable() {
    val sendDate = date("sendDate")
    val status = varchar("status", 32)
    val sender = varchar("sender", 64)
    val clientBusinessName = varchar("clientBusinessName", 64)
    val clientEmail = varchar("clientEmail", 64)
    val clientPhone = varchar("clientPhone", 64)
}

class Invoice(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Invoice>(Invoices)

    var sendDate by Invoices.sendDate
    var status by Invoices.status
    var sender by Invoices.sender
    var clientBusinessName by Invoices.clientBusinessName
    var clientEmail by Invoices.clientEmail
    var clientPhone by Invoices.clientPhone
}
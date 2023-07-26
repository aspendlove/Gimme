package storage

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection


class InvalidMatchSizeException(message: String) : Exception(message)

fun initDatabase(filename: String) {
    File(filename).createNewFile()
    Database.connect("jdbc:sqlite:$filename", "org.sqlite.JDBC")

    TransactionManager.manager.defaultIsolationLevel =
        Connection.TRANSACTION_SERIALIZABLE
    transaction {
        SchemaUtils.create(Clients, Users, Items, Invoices)
    }
}

fun insertUser(
    databaseFilename: String,
    inputBusinessName: String,
    inputContactName: String,
    inputSubtitle: String,
    inputStreet: String,
    inputCity: String,
    inputState: String,
    inputZip: Int,
    inputEmail: String,
    inputPhone: String,
): User {
    initDatabase(databaseFilename)

    return transaction {
        return@transaction User.new {
            businessName = inputBusinessName
            contactName = inputContactName
            subtitle = inputSubtitle
            street = inputStreet
            city = inputCity
            state = inputState
            zip = inputZip
            email = inputEmail
            phone = inputPhone
        }
    }
}

fun selectAllUsers(databaseFilename: String): List<User> {
    initDatabase(databaseFilename)
    return transaction {
        return@transaction User.all().sortedBy {
            it.businessName
        }.toList()
    }
}

fun selectUserById(databaseFilename: String, id: Int): User {
    initDatabase(databaseFilename)
    return transaction {
        val matches: List<User> = User.find(Users.id eq id).toList()
        if (matches.size == 1) {
            return@transaction matches[0]
        } else if (matches.size > 1) {
            throw InvalidMatchSizeException("Multiple matches with one id")
        } else {
            throw InvalidMatchSizeException("No match found")
        }
    }
}

fun deleteUserById(databaseFilename: String, id: Int) {
    initDatabase(databaseFilename)
    for (user: User in User.find(Users.id eq id)) {
        user.delete()
    }
}

fun insertClient(
    databaseFilename: String,
    inputBusinessName: String,
    inputContactName: String,
    inputStreet: String,
    inputCity: String,
    inputState: String,
    inputZip: Int,
    inputEmail: String,
    inputPhone: String,
): Client {
    initDatabase(databaseFilename)
    return transaction {
        return@transaction Client.new {
            businessName = inputBusinessName
            contactName = inputContactName
            street = inputStreet
            city = inputCity
            state = inputState
            zip = inputZip
            email = inputEmail
            phone = inputPhone
        }
    }
}

fun selectAllClients(databaseFilename: String): List<Client> {
    initDatabase(databaseFilename)
    return transaction {
        return@transaction Client.all().sortedBy {
            it.businessName
        }.toList()
    }
}

fun selectClientById(databaseFilename: String, id: Int): Client {
    initDatabase(databaseFilename)
    return transaction {
        val matches: List<Client> = Client.find(Clients.id eq id).toList()
        if (matches.size == 1) {
            return@transaction matches[0]
        } else if (matches.size > 1) {
            throw InvalidMatchSizeException("Multiple matches with one id")
        } else {
            throw InvalidMatchSizeException("No match found")
        }
    }
}

fun deleteClientById(databaseFilename: String,id: Int) {
    initDatabase(databaseFilename)
    for (client: Client in Client.find(Clients.id eq id)) {
        client.delete()
    }
}

fun insertItem(
    databaseFilename: String,
    inputStartDate: LocalDate,
    inputEndDate: LocalDate?,
    inputName: String,
    inputDescription: String,
    inputQuantity: Double,
    inputPrice: Double
): Item {
    initDatabase(databaseFilename)
    return transaction {
        return@transaction Item.new {
            startDate = inputStartDate
            endDate = inputEndDate
            name = inputName
            description = inputDescription
            quantity = inputQuantity
            price = inputPrice
        }
    }
}

fun selectAllItems(databaseFilename: String): List<Item> {
    initDatabase(databaseFilename)
    return transaction {
        return@transaction Item.all().sortedBy {
            it.name
        }.toList()
    }
}

fun selectItemById(databaseFilename: String,id: Int): Item {
    initDatabase(databaseFilename)
    return transaction {
        val matches: List<Item> = Item.find(Items.id eq id).toList()
        if (matches.size == 1) {
            return@transaction matches[0]
        } else if (matches.size > 1) {
            throw InvalidMatchSizeException("Multiple matches with one id")
        } else {
            throw InvalidMatchSizeException("No match found")
        }
    }
}

fun deleteItemById(databaseFilename:String ,id: Int) {
    initDatabase(databaseFilename)
    for (item: Item in Item.find(Items.id eq id)) {
        item.delete()
    }
}

fun insertInvoice(
    databaseFilename: String,
    inputSendDate: LocalDate,
    inputStatus: String,
    inputSender: String,
    inputClientBusinessName: String,
    inputClientEmail: String,
    inputClientPhone: String
): Invoice {
    initDatabase(databaseFilename)
    return transaction {
        return@transaction Invoice.new {
            sendDate = inputSendDate
            status = inputStatus
            sender = inputSender
            clientBusinessName = inputClientBusinessName
            clientEmail = inputClientEmail
            clientPhone = inputClientPhone
        }
    }
}

fun selectAllInvoices(databaseFilename: String): List<Invoice> {
    initDatabase(databaseFilename)
    return transaction {
        return@transaction Invoice.all().sortedBy {
            it.sendDate
        }.toList()
    }
}

fun selectInvoiceById(databaseFilename: String,id: Int): Invoice {
    initDatabase(databaseFilename)
    return transaction {
        val matches: List<Invoice> = Invoice.find(Invoices.id eq id).toList()
        if (matches.size == 1) {
            return@transaction matches[0]
        } else if (matches.size > 1) {
            throw InvalidMatchSizeException("Multiple matches with one id")
        } else {
            throw InvalidMatchSizeException("No match found")
        }
    }
}

fun deleteInvoiceById(databaseFilename: String,id: Int) {
    initDatabase(databaseFilename)
    transaction {
        for (invoice: Invoice in Invoice.find(Invoices.id eq id)) {
            invoice.delete()
        }
    }
}


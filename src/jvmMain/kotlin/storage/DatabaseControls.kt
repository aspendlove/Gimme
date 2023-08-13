package storage

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

fun doesTableExist(connection: Connection, tableName: String): Boolean {
    val query = "SELECT name FROM sqlite_master WHERE type='table' AND name=?;"
    connection.prepareStatement(query).use { preparedStatement ->
        preparedStatement.setString(1, tableName)
        return preparedStatement.executeQuery().next()
    }
}

fun createTables(connection: Connection) {


    if (!doesTableExist(connection, TableNames.clientTableName)) {
        connection.prepareStatement(
            """
            CREATE TABLE ${TableNames.clientTableName} (
            id INTEGER PRIMARY KEY,
            businessName VARCHAR(64),
            contactName VARCHAR(64),
            street VARCHAR(256),
            city VARCHAR(64),
            state VARCHAR(32),
            zip INTEGER,
            email VARCHAR(64),
            phone VARCHAR(32));"""
        ).use { query ->
            query.execute()
        }
    }

    if (!doesTableExist(connection, TableNames.userTableName)) {
        connection.prepareStatement(
            """
            CREATE TABLE ${TableNames.userTableName} (
            id INTEGER PRIMARY KEY,
            businessName VARCHAR(64),
            contactName VARCHAR(64),
            subtitle VARCHAR(4096),
            street VARCHAR(256),
            city VARCHAR(64),
            state VARCHAR(32),
            zip INTEGER,
            email VARCHAR(64),
            phone VARCHAR(32));"""
        ).use { query ->
            query.execute()
        }
    }

    if (!doesTableExist(connection, TableNames.itemTableName)) {
        connection.prepareStatement(
            """
            CREATE TABLE ${TableNames.itemTableName} (
            id INTEGER PRIMARY KEY,
            name VARCHAR(64),
            startDate DATE,
            endDate DATE,
            quantity DOUBLE PRECISION,
            price DECIMAL(10,2),
            description VARCHAR(4096));"""
        ).use { query ->
            query.execute()
        }
    }

    if (!doesTableExist(connection, TableNames.invoiceTableName)) {
        connection.prepareStatement(
            """
            CREATE TABLE ${TableNames.invoiceTableName} (
            id INTEGER PRIMARY KEY,
            sendDate DATE,
            status VARCHAR(32),
            sender VARCHAR(64),
            clientBusinessName VARCHAR(64),
            clientEmail VARCHAR(64),
            clientPhone VARCHAR(64));"""
        ).use { query ->
            query.execute()
        }
    }
}

fun insertInvoice(connection: Connection, invoice: Invoice): Int {
    val insertQuery =
        "INSERT INTO ${TableNames.invoiceTableName} (sendDate, status, sender, clientBusinessName, clientEmail, clientPhone) VALUES (?, ?, ?, ?, ?, ?)"

    connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS).use { preparedStatement ->
        with(preparedStatement) {
            setDate(1, invoice.sendDate)
            setString(2, invoice.status)
            setString(3, invoice.sender)
            setString(4, invoice.clientBusinessName)
            setString(5, invoice.clientEmail)
            setString(6, invoice.clientPhone)
            executeUpdate()
        }
        val generatedKeys: ResultSet = preparedStatement.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1)
        } else {
            throw SQLInsertException("Failed to get primary key after insertion")
        }
    }
}

fun insertUser(connection: Connection, user: User): Int {
    val insertQuery =
        "INSERT INTO ${TableNames.userTableName} (businessName, contactName, subtitle, street, city, state, zip, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"

    connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS).use { preparedStatement ->
        with(preparedStatement) {
            setString(1, user.businessName)
            setString(2, user.contactName)
            setString(3, user.subtitle)
            setString(4, user.street)
            setString(5, user.city)
            setString(6, user.state)
            setInt(7, user.zip)
            setString(8, user.email)
            setString(9, user.phone)
            executeUpdate()
        }
        val generatedKeys: ResultSet = preparedStatement.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1)
        } else {
            throw SQLInsertException("Failed to get primary key after insertion")
        }
    }
}

fun insertClient(connection: Connection, client: Client): Int {
    val insertQuery =
        "INSERT INTO ${TableNames.clientTableName} (businessName, contactName, street, city, state, zip, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"

    connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS).use { preparedStatement ->
        with(preparedStatement) {
            setString(1, client.businessName)
            setString(2, client.contactName)
            setString(3, client.street)
            setString(4, client.city)
            setString(5, client.state)
            setInt(6, client.zip)
            setString(7, client.email)
            setString(8, client.phone)
            executeUpdate()
        }
        val generatedKeys: ResultSet = preparedStatement.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1)
        } else {
            throw SQLInsertException("Failed to get primary key after insertion")
        }
    }
}

fun insertItem(connection: Connection, item: Item): Int {
    val insertQuery =
        "INSERT INTO ${TableNames.itemTableName} (name, startDate, endDate, quantity, price, description) VALUES (?, ?, ?, ?, ?, ?)"
    connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS).use { preparedStatement ->
        with(preparedStatement) {
            setString(1, item.name)
            setDate(2, item.startDate)
            setDate(3, item.endDate)
            setDouble(4, item.quantity)
            setBigDecimal(5, item.price)
            setString(6, item.description)
            executeUpdate()
        }
        val generatedKeys: ResultSet = preparedStatement.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1)
        } else {
            throw SQLInsertException("Failed to get primary key after insertion")
        }
    }
}

fun selectAllInvoices(connection: Connection, columnToSortBy: String): List<Invoice> {
    connection.prepareStatement("SELECT * FROM ${TableNames.invoiceTableName} ORDER BY $columnToSortBy ASC")
        .use { preparedStatement ->
            val results = preparedStatement.executeQuery()
            val returnList = mutableListOf<Invoice>()
            while (results.next()) {
                returnList.add(
                    invoiceFromResult(results)
                )
            }
            return returnList
        }
}

fun selectAllUsers(connection: Connection, columnToSortBy: String): List<User> {
    connection.prepareStatement("SELECT * FROM ${TableNames.userTableName} ORDER BY $columnToSortBy ASC")
        .use { preparedStatement ->
            val results = preparedStatement.executeQuery()
            val returnList = mutableListOf<User>()
            while (results.next()) {
                returnList.add(
                    userFromResult(results)
                )
            }
            return returnList
        }
}

fun selectAllClients(connection: Connection, columnToSortBy: String): List<Client> {
    connection.prepareStatement("SELECT * FROM ${TableNames.clientTableName} ORDER BY $columnToSortBy ASC")
        .use { preparedStatement ->
            val results = preparedStatement.executeQuery()
            val returnList = mutableListOf<Client>()
            while (results.next()) {
                returnList.add(
                    clientFromResult(results)
                )
            }
            return returnList
        }
}

fun selectAllItems(connection: Connection, columnToSortBy: String): List<Item> {
    connection.prepareStatement("SELECT * FROM ${TableNames.itemTableName} ORDER BY $columnToSortBy ASC")
        .use { preparedStatement ->
            val results = preparedStatement.executeQuery()
            val returnList = mutableListOf<Item>()
            while (results.next()) {
                returnList.add(
                    itemFromResult(results)
                )
            }
            return returnList
        }
}

fun selectInvoice(connection: Connection, id: Int): Invoice {
    connection.prepareStatement(
        "SELECT * FROM ${TableNames.invoiceTableName} WHERE id = $id"
    ).use { preparedStatement ->
        val result = preparedStatement.executeQuery()
        if (!result.next()) throw NoResultException("no row with given id")
        return invoiceFromResult(result)
    }
}

fun selectUser(connection: Connection, id: Int): User {
    connection.prepareStatement(
        "SELECT * FROM ${TableNames.userTableName} WHERE id = $id"
    ).use { preparedStatement ->
        val result = preparedStatement.executeQuery()
        if (!result.first()) throw NoResultException("no row with given id")
        return userFromResult(result)
    }
}

fun selectClient(connection: Connection, id: Int): Client {
    connection.prepareStatement(
        "SELECT * FROM ${TableNames.clientTableName} WHERE id = $id"
    ).use { preparedStatement ->
        val result = preparedStatement.executeQuery()
        if (!result.first()) throw NoResultException("no row with given id")
        return clientFromResult(result)
    }
}

fun selectItem(connection: Connection, id: Int): Item {
    connection.prepareStatement(
        "SELECT * FROM ${TableNames.itemTableName} WHERE id = $id"
    ).use { preparedStatement ->
        val result = preparedStatement.executeQuery()
        if (!result.first()) throw NoResultException("no row with given id")
        return itemFromResult(result)
    }
}

private fun deleteRowById(connection: Connection, tableName: String, id: Int): Boolean {
    connection.prepareStatement("DELETE FROM $tableName WHERE id = ?").use { preparedStatement ->
        preparedStatement.setInt(1, id)
        val rowsAffected = preparedStatement.executeUpdate()
        return rowsAffected > 0
    }
}

fun deleteInvoice(connection: Connection, id: Int): Boolean {
    return deleteRowById(connection, TableNames.invoiceTableName, id)
}

fun deleteUser(connection: Connection, id: Int): Boolean {
    return deleteRowById(connection, TableNames.userTableName, id)
}

fun deleteClient(connection: Connection, id: Int): Boolean {
    return deleteRowById(connection, TableNames.clientTableName, id)
}

fun deleteItem(connection: Connection, id: Int): Boolean {
    return deleteRowById(connection, TableNames.itemTableName, id)
}

// TODO test generic
fun <T> searchClients(connection: Connection, columnName: String, query: T): List<Client> {
    val returnList = mutableListOf<Client>()

    connection.prepareStatement("SELECT * FROM ${TableNames.clientTableName} WHERE $columnName LIKE ?")
        .use { preparedStatement ->
            preparedStatement.setString(1, columnName)
            preparedStatement.setString(2, "%$query%")
            val results = preparedStatement.executeQuery()

            while (results.next()) {
                returnList.add(clientFromResult(results))
            }
        }

    return returnList
}

fun <T> searchUsers(connection: Connection, columnName: String, query: T): List<User> {
    val returnList = mutableListOf<User>()

    connection.prepareStatement("SELECT * FROM ${TableNames.userTableName} WHERE $columnName LIKE ?")
        .use { preparedStatement ->
            preparedStatement.setString(1, columnName)
            preparedStatement.setString(2, "%$query%")
            val results = preparedStatement.executeQuery()

            while (results.next()) {
                returnList.add(userFromResult(results))
            }
        }

    return returnList
}

fun <T> searchInvoices(connection: Connection, columnName: String, query: T): List<Invoice> {
    val returnList = mutableListOf<Invoice>()

    connection.prepareStatement("SELECT * FROM ${TableNames.invoiceTableName} WHERE $columnName LIKE ?")
        .use { preparedStatement ->
//            preparedStatement.setString(1, columnName)
            preparedStatement.setString(1, "%$query%")
            val results = preparedStatement.executeQuery()

            while (results.next()) {
                returnList.add(invoiceFromResult(results))
            }
        }

    return returnList
}

fun <T> searchItems(connection: Connection, columnName: String, query: T): List<Item> {
    val returnList = mutableListOf<Item>()

    connection.prepareStatement("SELECT * FROM ${TableNames.itemTableName} WHERE $columnName LIKE ?")
        .use { preparedStatement ->
            preparedStatement.setString(1, columnName)
            preparedStatement.setString(2, "%$query%")
            val results = preparedStatement.executeQuery()

            while (results.next()) {
                returnList.add(itemFromResult(results))
            }
        }

    return returnList
}

fun invoiceFromResult(result: ResultSet): Invoice {
    try {
        return Invoice(
            result.getDate(InvoiceColumns.sendDate),
            result.getString(InvoiceColumns.status),
            result.getString(InvoiceColumns.sender),
            result.getString(InvoiceColumns.clientBusinessName),
            result.getString(InvoiceColumns.clientEmail),
            result.getString(InvoiceColumns.clientPhone),
            id = result.getInt(InvoiceColumns.id)
        )
    } catch (e: SQLException) {
        throw SQLDataRetrievalException("Row Mapping is Incorrect")
    }
}

fun userFromResult(result: ResultSet): User {
    try {
        return User(
            result.getString(UserColumns.businessName),
            result.getString(UserColumns.contactName),
            result.getString(UserColumns.subtitle),
            result.getString(UserColumns.street),
            result.getString(UserColumns.city),
            result.getString(UserColumns.state),
            result.getInt(UserColumns.zip),
            result.getString(UserColumns.email),
            result.getString(UserColumns.phone),
            id = result.getInt(UserColumns.id)
        )
    } catch (e: SQLException) {
        throw SQLDataRetrievalException("Row Mapping is Incorrect")
    }
}

fun clientFromResult(result: ResultSet): Client {
    try {
        return Client(
            result.getString(ClientColumns.businessName),
            result.getString(ClientColumns.contactName),
            result.getString(ClientColumns.street),
            result.getString(ClientColumns.city),
            result.getString(ClientColumns.state),
            result.getInt(ClientColumns.zip),
            result.getString(ClientColumns.email),
            result.getString(ClientColumns.phone),
            id = result.getInt(ClientColumns.id)
        )
    } catch (e: SQLException) {
        throw SQLDataRetrievalException("Row Mapping is Incorrect")
    }
}

fun itemFromResult(result: ResultSet): Item {
    try {
        return Item(
            result.getString(ItemColumns.name),
            result.getDate(ItemColumns.startDate),
            result.getDate(ItemColumns.endDate),
            result.getDouble(ItemColumns.quantity),
            result.getBigDecimal(ItemColumns.price),
            result.getString(ItemColumns.description),
            id = result.getInt(ItemColumns.id)
        )
    } catch (e: SQLException) {
        throw SQLDataRetrievalException("Row Mapping is Incorrect")
    }
}

object TableNames {
    const val invoiceTableName: String = "Invoices"
    const val userTableName: String = "Users"
    const val clientTableName: String = "Clients"
    const val itemTableName: String = "Items"
}

object InvoiceColumns {
    const val sendDate: String = "sendDate"
    const val status: String = "status"
    const val sender: String = "sender"
    const val clientBusinessName: String = "clientBusinessName"
    const val clientEmail: String = "clientEmail"
    const val clientPhone: String = "clientPhone"
    const val id: String = "id"
}

object UserColumns {
    const val businessName: String = "businessName"
    const val contactName: String = "contactName"
    const val subtitle: String = "subtitle"
    const val street: String = "street"
    const val city: String = "city"
    const val state: String = "state"
    const val zip: String = "zip"
    const val email: String = "email"
    const val phone: String = "phone"
    const val id: String = "id"
}

object ClientColumns {
    const val businessName: String = "businessName"
    const val contactName: String = "contactName"
    const val street: String = "street"
    const val city: String = "city"
    const val state: String = "state"
    const val zip: String = "zip"
    const val email: String = "email"
    const val phone: String = "phone"
    const val id: String = "id"
}

object ItemColumns {
    const val name: String = "name"
    const val startDate: String = "startDate"
    const val endDate: String = "endDate"
    const val quantity: String = "quantity"
    const val price: String = "price"
    const val description: String = "description"
    const val id: String = "id"
}

class NoResultException(message: String) : Exception(message)

class SQLInsertException(message: String) : Exception(message)

class SQLDataRetrievalException(message: String) : Exception(message)


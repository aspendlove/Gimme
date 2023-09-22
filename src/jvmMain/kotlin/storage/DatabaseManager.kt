package storage

import java.io.File
import java.sql.*

object DatabaseManager {
    private var UserCache: MutableList<User?> = mutableListOf()
    private var ClientCache: MutableList<Client?> = mutableListOf()
    private var ItemCache: MutableList<Item?> = mutableListOf()
    private var InvoiceCache: MutableList<Invoice?> = mutableListOf()

    private var _databaseFilePath: String = "GimmeDatabase.db"
    var databaseFilePath: String
        get() {
            return _databaseFilePath
        }
        set(newFilePath) {
            _databaseFilePath = newFilePath
            invalidateAllCaches()
        }

    init {
        UserCache = selectAllUsers(cache = false).toMutableList()
        ClientCache = selectAllClients(cache = false).toMutableList()
        ItemCache = selectAllItems(cache = false).toMutableList()
        InvoiceCache = selectAllInvoices(cache = false).toMutableList()
    }

    /**
     * For testing purposes only
     * To delete, areYouSure must be "DELETE THE DATABASE"
     */
    fun wipeDatabase(areYouSure: String) {
        if (areYouSure != "DELETE THE DATABASE") return
        val databaseFile = File(databaseFilePath)
        if (databaseFile.exists()) {
            databaseFile.delete()
        }
        invalidateAllCaches()
    }

    private fun invalidateAllCaches() {
        invalidateUserCache()
        invalidateClientCache()
        invalidateItemCache()
        invalidateInvoiceCache()
    }

    private fun invalidateUserCache() {
        UserCache.clear()
        UserCache = selectAllUsers(cache = false).toMutableList()
    }

    private fun invalidateClientCache() {
        ClientCache.clear()
        ClientCache = selectAllClients(cache = false).toMutableList()
    }

    private fun invalidateItemCache() {
        ItemCache.clear()
        ItemCache = selectAllItems(cache = false).toMutableList()
    }

    private fun invalidateInvoiceCache() {
        InvoiceCache.clear()
        InvoiceCache = selectAllInvoices(cache = false).toMutableList()
    }

    private fun nullifyUserCacheElement(id: Int) {
        if (id - 1 >= UserCache.size) return
        UserCache[id - 1] = null
    }

    private fun nullifyClientCacheElement(id: Int) {
        if (id - 1 >= ClientCache.size) return
        ClientCache[id - 1] = null
    }

    private fun nullifyItemCacheElement(id: Int) {
        if (id - 1 >= ItemCache.size) return
        ItemCache[id - 1] = null
    }

    private fun nullifyInvoiceCacheElement(id: Int) {
        if (id - 1 >= InvoiceCache.size) return
        InvoiceCache[id - 1] = null
    }

    private fun connect(): Connection {
        val connection = DriverManager.getConnection("jdbc:sqlite:$databaseFilePath")
        createTables(connection)
        return connection
    }

    /**
     * Checks that a table with the passed name exists
     *
     * @param tableName
     * @return
     */
    private fun doesTableExist(connection: Connection, tableName: String): Boolean {
        val query = "SELECT name FROM sqlite_master WHERE type='table' AND name=?;"
        connection.prepareStatement(query).use { preparedStatement ->
            preparedStatement.setString(1, tableName)
            return preparedStatement.executeQuery().next()
        }
    }

    /**
     * Creates all needed tables if they do not already exist in the database
     *
     */
    private fun createTables(connection: Connection) {
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

    /**
     * Inserts an invoice into the correct table and returns the ID of the newly inserted row
     *
     * @param invoice invoice object that will be inserted
     * @return ID of newly inserted row
     * @throws SQLInsertException Could not get the ID after insertion
     */
    fun insertInvoice(invoice: Invoice): Int {
        connect().use { connection ->
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
                invalidateInvoiceCache()
                val generatedKeys: ResultSet = preparedStatement.generatedKeys
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1)
                } else {
                    throw SQLInsertException("Failed to get primary key after insertion")
                }
            }
        }
    }

    /**
     * Inserts a user into the correct table and returns the ID of the newly inserted row
     *
     * @param user user object that will be inserted
     * @return ID of newly inserted row
     * @throws SQLInsertException Could not get the ID after insertion
     */
    fun insertUser(user: User): Int {
        connect().use { connection ->
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
                invalidateUserCache()
                val generatedKeys: ResultSet = preparedStatement.generatedKeys
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1)
                } else {
                    throw SQLInsertException("Failed to get primary key after insertion")
                }
            }
        }
    }

    /**
     * Inserts a client into the correct table and returns the ID of the newly inserted row
     *
     * @param client client object that will be inserted
     * @return ID of newly inserted row
     * @throws SQLInsertException Could not get the ID after insertion
     */
    fun insertClient(client: Client): Int {
        connect().use { connection ->
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
                invalidateClientCache()
                val generatedKeys: ResultSet = preparedStatement.generatedKeys
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1)
                } else {
                    throw SQLInsertException("Failed to get primary key after insertion")
                }
            }
        }
    }

    /**
     * Inserts an item into the correct table and returns the ID of the newly inserted row
     *
     * @param item item object that will be inserted
     * @return ID of newly inserted row
     * @throws SQLInsertException Could not get the ID after insertion
     */
    fun insertItem(item: Item): Int {
        connect().use { connection ->
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
                invalidateItemCache()
                val generatedKeys: ResultSet = preparedStatement.generatedKeys
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1)
                } else {
                    throw SQLInsertException("Failed to get primary key after insertion")
                }
            }
        }
    }

    /**
     * Returns all invoices ordered by the given column name either ascending or descending
     *
     * @param columnToSortBy the name of the column that will be used to sort the results
     * @param ascending whether to sort by ascending or not
     * @return a list of invoice objects
     */
    fun selectAllInvoices(
        columnToSortBy: String = InvoiceColumns.id,
        ascending: Boolean = true,
        cache: Boolean = true
    ): List<Invoice> {
        if (columnToSortBy == InvoiceColumns.id && cache) {
            return if (ascending) {
                InvoiceCache.filterNotNull()
            } else {
                InvoiceCache.reversed().filterNotNull()
            }
        }

        var sortDirection = "DESC"
        if (ascending) {
            sortDirection = "ASC"
        }

        connect().use { connection ->
            connection.prepareStatement("SELECT * FROM ${TableNames.invoiceTableName} ORDER BY $columnToSortBy $sortDirection")
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
    }

    /**
     * Returns all users ordered by the given column name either ascending or descending
     *
     * @param columnToSortBy the name of the column that will be used to sort the results
     * @param ascending whether to sort by ascending or not
     * @return a users of invoice objects
     */
    fun selectAllUsers(
        columnToSortBy: String = UserColumns.id,
        ascending: Boolean = true,
        cache: Boolean = true
    ): List<User> {
        if (columnToSortBy == UserColumns.id && cache) {
            return if (ascending) {
                UserCache.filterNotNull()
            } else {
                UserCache.reversed().filterNotNull()
            }
        }

        var sortDirection = "DESC"
        if (ascending) {
            sortDirection = "ASC"
        }

        connect().use { connection ->
            connection.prepareStatement("SELECT * FROM ${TableNames.userTableName} ORDER BY $columnToSortBy $sortDirection")
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
    }

    /**
     * Returns all clients ordered by the given column name either ascending or descending
     *
     * @param columnToSortBy the name of the column that will be used to sort the results
     * @param ascending whether to sort by ascending or not
     * @return a list of client objects
     */
    fun selectAllClients(
        columnToSortBy: String = ClientColumns.id,
        ascending: Boolean = true,
        cache: Boolean = true
    ): List<Client> {
        if (columnToSortBy == ClientColumns.id && cache) {
            return if (ascending) {
                ClientCache.filterNotNull()
            } else {
                ClientCache.reversed().filterNotNull()
            }
        }

        var sortDirection = "DESC"
        if (ascending) {
            sortDirection = "ASC"
        }

        connect().use { connection ->
            connection.prepareStatement("SELECT * FROM ${TableNames.clientTableName} ORDER BY $columnToSortBy $sortDirection")
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
    }

    /**
     * Returns all items ordered by the given column name either ascending or descending
     *
     * @param columnToSortBy the name of the column that will be used to sort the results
     * @param ascending whether to sort by ascending or not
     * @return a list of item objects
     */
    fun selectAllItems(
        columnToSortBy: String = ItemColumns.id,
        ascending: Boolean = true,
        cache: Boolean = true
    ): List<Item> {
        if (columnToSortBy == ItemColumns.id && cache) {
            return if (ascending) {
                ItemCache.filterNotNull()
            } else {
                ItemCache.reversed().filterNotNull()
            }
        }

        var sortDirection = "DESC"
        if (ascending) {
            sortDirection = "ASC"
        }
        connect().use { connection ->
            connection.prepareStatement("SELECT * FROM ${TableNames.itemTableName} ORDER BY $columnToSortBy $sortDirection")
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
    }

    /**
     * Select a single invoice with a given id
     *
     * @param id
     * @return
     */
    fun selectInvoice(id: Int): Invoice {
        if (InvoiceCache.size >= id && id >= 1) {
            val cacheHit = InvoiceCache[id - 1]
            if (cacheHit != null) {
                return cacheHit
            } else {
                throw NoResultException("no row with given id")
            }
        }

        connect().use { connection ->
            connection.prepareStatement(
                "SELECT * FROM ${TableNames.invoiceTableName} WHERE id = $id"
            ).use { preparedStatement ->
                val result = preparedStatement.executeQuery()
                if (!result.next()) throw NoResultException("no row with given id")
                return invoiceFromResult(result)
            }
        }
    }

    /**
     * Select a single user with a given id
     *
     * @param id
     * @return
     */
    fun selectUser(id: Int): User {
        if (UserCache.size >= id && id >= 1) {
            val cacheHit = UserCache[id - 1]
            if (cacheHit != null) {
                return cacheHit
            } else {
                throw NoResultException("no row with given id")
            }
        }

        connect().use { connection ->
            connection.prepareStatement(
                "SELECT * FROM ${TableNames.userTableName} WHERE id = $id"
            ).use { preparedStatement ->
                val result = preparedStatement.executeQuery()
                if (!result.next()) throw NoResultException("no row with given id")
                return userFromResult(result)
            }
        }
    }

    /**
     * Select a single client with a given id
     *
     * @param id
     * @return
     */
    fun selectClient(id: Int): Client {
        if (ClientCache.size >= id && id >= 1) {
            val cacheHit = ClientCache[id - 1]
            if (cacheHit != null) {
                return cacheHit
            } else {
                throw NoResultException("no row with given id")
            }
        }

        connect().use { connection ->
            connection.prepareStatement(
                "SELECT * FROM ${TableNames.clientTableName} WHERE id = $id"
            ).use { preparedStatement ->
                val result = preparedStatement.executeQuery()
                if (!result.next()) throw NoResultException("no row with given id")
                return clientFromResult(result)
            }
        }
    }

    /**
     * Select a single item with a given id
     *
     * @param id
     * @return
     */
    fun selectItem(id: Int): Item {
        if (ItemCache.size >= id && id >= 1) {
            val cacheHit = ItemCache[id - 1]
            if (cacheHit != null) {
                return cacheHit
            } else {
                throw NoResultException("no row with given id")
            }
        }

        connect().use { connection ->
            connection.prepareStatement(
                "SELECT * FROM ${TableNames.itemTableName} WHERE id = $id"
            ).use { preparedStatement ->
                val result = preparedStatement.executeQuery()
                if (!result.next()) throw NoResultException("no row with given id")
                return itemFromResult(result)
            }
        }
    }

    /**
     * Delete a single row by id
     *
     * @param tableName the name of the table to delete a row in
     * @param id id of the row to delete
     * @return whether any rows were deleted
     */
    private fun deleteRowById(
        tableName: String,
        id: Int,
        nullifyCacheElement: (Int) -> Unit
    ): Boolean {
        connect().use { connection ->
            connection.prepareStatement("DELETE FROM $tableName WHERE id = ?").use { preparedStatement ->
                preparedStatement.setInt(1, id)
                val rowsAffected = preparedStatement.executeUpdate()
                nullifyCacheElement(id)
                return rowsAffected > 0
            }
        }
    }

    /**
     * Delete a single invoice by id
     *
     * @param id
     * @return whether any rows were deleted
     */
    fun deleteInvoice(id: Int): Boolean {
        return deleteRowById(TableNames.invoiceTableName, id, ::nullifyInvoiceCacheElement)
    }

    /**
     * Delete a single user by id
     *
     * @param id
     * @return whether any rows were deleted
     */
    fun deleteUser(id: Int): Boolean {
        return deleteRowById(TableNames.userTableName, id, ::nullifyUserCacheElement)

    }

    /**
     * Delete a single client by id
     *
     * @param id
     * @return whether any rows were deleted
     */
    fun deleteClient(id: Int): Boolean {
        return deleteRowById(TableNames.clientTableName, id, ::nullifyClientCacheElement)
    }

    /**
     * Delete a single client by id
     *
     * @param id
     * @return
     */
    fun deleteItem(id: Int): Boolean {
        return deleteRowById(TableNames.itemTableName, id, ::nullifyItemCacheElement)
    }

    /**
     * Search the clients table for a given value in a given column. The specified query must be a part or a whole of a
     * value in the table. If the value contains a difference from one of the values, it is not a valid match.
     *
     * @param T the data type of the given column name
     * @param columnName the column name to search
     * @param query the query to be searched against the clients table. It is of the generic type given.
     * @return
     */
    fun <T> searchClients(columnName: String, query: T): List<Client> {
        val returnList = mutableListOf<Client>()

        connect().use { connection ->
            connection.prepareStatement("SELECT * FROM ${TableNames.clientTableName} WHERE $columnName LIKE ?")
                .use { preparedStatement ->
                    preparedStatement.setString(1, "%$query%")
                    val results = preparedStatement.executeQuery()

                    while (results.next()) {
                        returnList.add(clientFromResult(results))
                    }
                }
        }

        return returnList
    }

    /**
     * Search the users table for a given value in a given column. The specified query must be a part or a whole of a
     * value in the table. If the value contains a difference from one of the values, it is not a valid match.
     *
     * @param T the data type of the given column name
     * @param columnName the column name to search
     * @param query the query to be searched against the users table. It is of the generic type given.
     * @return
     */
    fun <T> searchUsers(columnName: String, query: T): List<User> {
        val returnList = mutableListOf<User>()

        connect().use { connection ->
            connection.prepareStatement("SELECT * FROM ${TableNames.userTableName} WHERE $columnName LIKE ?")
                .use { preparedStatement ->
                    preparedStatement.setString(1, "%$query%")
                    val results = preparedStatement.executeQuery()

                    while (results.next()) {
                        returnList.add(userFromResult(results))
                    }
                }
        }


        return returnList
    }

    /**
     * Search the invoices table for a given value in a given column. The specified query must be a part or a whole of a
     * value in the table. If the value contains a difference from one of the values, it is not a valid match.
     *
     * @param T the data type of the given column name
     * @param columnName the column name to search
     * @param query the query to be searched against the invoices table. It is of the generic type given.
     * @return
     */
    fun <T> searchInvoices(columnName: String, query: T): List<Invoice> {
        val returnList = mutableListOf<Invoice>()

        connect().use { connection ->
            connection.prepareStatement("SELECT * FROM ${TableNames.invoiceTableName} WHERE $columnName LIKE ?")
                .use { preparedStatement ->
                    preparedStatement.setString(1, "%$query%")
                    val results = preparedStatement.executeQuery()

                    while (results.next()) {
                        returnList.add(invoiceFromResult(results))
                    }
                }
        }


        return returnList
    }

    /**
     * Search the items table for a given value in a given column. The specified query must be a part or a whole of a
     * value in the table. If the value contains a difference from one of the values, it is not a valid match.
     *
     * @param T the data type of the given column name
     * @param columnName the column name to search
     * @param query the query to be searched against the items table. It is of the generic type given.
     * @return
     */
    fun <T> searchItems(columnName: String, query: T): List<Item> {
        val returnList = mutableListOf<Item>()

        connect().use { connection ->
            connection.prepareStatement("SELECT * FROM ${TableNames.itemTableName} WHERE $columnName LIKE ?")
                .use { preparedStatement ->
                    preparedStatement.setString(1, "%$query%")
                    val results = preparedStatement.executeQuery()

                    while (results.next()) {
                        returnList.add(itemFromResult(results))
                    }
                }
        }

        return returnList
    }

    /**
     * Produces a single invoice object from a ResultSet's current position
     *
     * @param result a JDBC result set
     * @return
     */
    private fun invoiceFromResult(result: ResultSet): Invoice {
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

    /**
     * Produces a single user object from a ResultSet's current position
     *
     * @param result a JDBC result set
     * @return
     */
    private fun userFromResult(result: ResultSet): User {
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

    /**
     * Produces a single client object from a ResultSet's current position
     *
     * @param result a JDBC result set
     * @return
     */
    private fun clientFromResult(result: ResultSet): Client {
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

    /**
     * Produces a single item object from a ResultSet's current position
     *
     * @param result a JDBC result set
     * @return
     */
    private fun itemFromResult(result: ResultSet): Item {
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
}

/**
 * A consistent place for the names of the tables
 *
 * @constructor Create empty Table names
 */
object TableNames {
    const val invoiceTableName: String = "Invoices"
    const val userTableName: String = "Users"
    const val clientTableName: String = "Clients"
    const val itemTableName: String = "Items"
}

/**
 * A consistent place for the names of the invoice table's column names
 *
 * @constructor Create empty Invoice columns
 */
object InvoiceColumns {
    const val sendDate: String = "sendDate"
    const val status: String = "status"
    const val sender: String = "sender"
    const val clientBusinessName: String = "clientBusinessName"
    const val clientEmail: String = "clientEmail"
    const val clientPhone: String = "clientPhone"
    const val id: String = "id"
}

/**
 * A consistent place for the names of the user table's column names
 *
 * @constructor Create empty Invoice columns
 */
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

/**
 * A consistent place for the names of the client table's column names
 *
 * @constructor Create empty Invoice columns
 */
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

/**
 * A consistent place for the names of the item table's column names
 *
 * @constructor Create empty Invoice columns
 */
object ItemColumns {
    const val name: String = "name"
    const val startDate: String = "startDate"
    const val endDate: String = "endDate"
    const val quantity: String = "quantity"
    const val price: String = "price"
    const val description: String = "description"
    const val id: String = "id"
}

/**
 * Thrown when a select operation returns no result
 *
 * @constructor
 *
 * @param message
 */
class NoResultException(message: String) : Exception(message)

/**
 * Thrown when a SQL insert operation fails
 *
 * @constructor
 *
 * @param message
 */
class SQLInsertException(message: String) : Exception(message)

/**
 * Thrown when an attempt is made to retrieve data from a non-existent column
 *
 * @constructor
 *
 * @param message
 */
class SQLDataRetrievalException(message: String) : Exception(message)


package storage

import java.io.File
import java.sql.*

// TODO add fully featured invoice tracking (enough info to recreate a full pdf from an entry in the invoice table)
// TODO add apartment info to addresses
object DatabaseManager {
    private var UserCache: MutableList<User?> = mutableListOf()
    private var ClientCache: MutableList<Client?> = mutableListOf()
    private var ItemCache: MutableList<Item?> = mutableListOf()
    private var NoteCache: MutableList<String?> = mutableListOf()
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
        invalidateAllCaches()
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

    private fun invalidateNoteCache() {
        NoteCache.clear()
        NoteCache = selectAllNotes(cache = false).toMutableList()
    }

    private fun invalidateItemCache() {
        ItemCache.clear()
        ItemCache = selectAllItems(cache = false).toMutableList()
    }

    private fun invalidateInvoiceCache() {
        InvoiceCache.clear()
        InvoiceCache = selectAllInvoices(cache = false).toMutableList()
    }

    private fun <T> createCacheElementNullifier(cache: MutableList<T?>): (Int) -> Unit {
        return fun(id: Int) { nullifyCacheElement<T>(id, cache) }
    }

    private fun <T> nullifyCacheElement(id: Int, cache: MutableList<T?>) {
        if (id - 1 >= cache.size) return
        cache[id - 1] = null
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
        val tables = listOf(
            Pair(
                TableNames.CLIENT_TABLE_NAME, """
            CREATE TABLE ${TableNames.CLIENT_TABLE_NAME} (
            id INTEGER PRIMARY KEY,
            businessName VARCHAR(64),
            contactName VARCHAR(64),
            street VARCHAR(256),
            city VARCHAR(64),
            state VARCHAR(32),
            zip INTEGER,
            email VARCHAR(64),
            phone VARCHAR(32));"""
            ),
            Pair(
                TableNames.USER_TABLE_NAME, """
            CREATE TABLE ${TableNames.USER_TABLE_NAME} (
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
            ),
            Pair(
                TableNames.NOTE_TABLE_NAME, """
            CREATE TABLE ${TableNames.NOTE_TABLE_NAME} (
            id INTEGER PRIMARY KEY,
            note VARCHAR(1000));"""
            ),
            Pair(
                TableNames.ITEM_TABLE_NAME, """
            CREATE TABLE ${TableNames.ITEM_TABLE_NAME} (
            id INTEGER PRIMARY KEY,
            name VARCHAR(64),
            startDate DATE,
            endDate DATE,
            quantity DOUBLE PRECISION,
            price DECIMAL(10,2),
            description VARCHAR(4096));"""
            ),
            Pair(
                TableNames.INVOICE_TABLE_NAME, """
            CREATE TABLE ${TableNames.INVOICE_TABLE_NAME} (
            id INTEGER PRIMARY KEY,
            sendDate DATE,
            status VARCHAR(32),
            sender VARCHAR(64),
            clientBusinessName VARCHAR(64),
            clientEmail VARCHAR(64),
            clientPhone VARCHAR(64));"""
            )
        )

        for (table in tables) {
            if (!doesTableExist(connection, table.first)) {
                connection.prepareStatement(
                    table.second
                ).use { query ->
                    query.execute()
                }
            }
        }
    }

    private fun makeInsertString(tableName: String, fields: List<String>): String {
        var fieldString = "("
        var questionMarkString = "("
        for (field in fields) {
            fieldString += "$field, "
            questionMarkString += "?, "
        }
        // remove the trailing commas and spaces, and then close the parenthesis
        fieldString = fieldString.removeSuffix(", ")
        fieldString += ")"
        questionMarkString = questionMarkString.removeSuffix(", ")
        questionMarkString += ")"

        return "INSERT INTO $tableName $fieldString VALUES $questionMarkString"
    }

    private fun insert(
        fields: List<String>,
        tableName: String,
        resultInterpreter: (PreparedStatement) -> Unit,
        invalidateCache: () -> Unit
    ): Int {
        connect().use { connection ->
            val insertQuery = makeInsertString(tableName, fields)
            connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS).use { preparedStatement ->
                resultInterpreter(preparedStatement)
                invalidateCache()
                val generatedKeys: ResultSet = preparedStatement.generatedKeys
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1)
                } else {
                    throw SQLInsertException("Failed to get primary key after insertion")
                }
            }
        }
    }

    fun insertUser(user: User): Int {
        return insert(
            UserColumns.columnList, TableNames.USER_TABLE_NAME,
            { preparedStatement: PreparedStatement ->
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
            },
            ::invalidateUserCache
        )
    }

    fun insertClient(client: Client): Int {
        return insert(
            ClientColumns.columnList, TableNames.CLIENT_TABLE_NAME,
            { preparedStatement: PreparedStatement ->
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
            },
            ::invalidateClientCache
        )
    }

    fun insertNote(noteText: String): Int {
        return insert(
            NoteColumns.columnList, TableNames.NOTE_TABLE_NAME,
            { preparedStatement: PreparedStatement ->
                with(preparedStatement) {
                    setString(1, noteText)
                    executeUpdate()
                }
            },
            ::invalidateNoteCache
        )
    }

    fun insertItem(item: Item): Int {
        return insert(
            ItemColumns.columnList, TableNames.ITEM_TABLE_NAME,
            { preparedStatement: PreparedStatement ->
                with(preparedStatement) {
                    setString(1, item.name)
                    setDate(2, item.startDate)
                    setDate(3, item.endDate)
                    setDouble(4, item.quantity)
                    setBigDecimal(5, item.price)
                    setString(6, item.description)
                    executeUpdate()
                }
            },
            ::invalidateItemCache
        )
    }

    fun insertInvoice(invoice: Invoice): Int {
        return insert(
            InvoiceColumns.columnList, TableNames.INVOICE_TABLE_NAME,
            { preparedStatement: PreparedStatement ->
                with(preparedStatement) {
                    setDate(1, invoice.sendDate)
                    setString(2, invoice.status)
                    setString(3, invoice.sender)
                    setString(4, invoice.clientBusinessName)
                    setString(5, invoice.clientEmail)
                    setString(6, invoice.clientPhone)

                    executeUpdate()
                }
            },
            ::invalidateInvoiceCache
        )
    }

    /**
     * Returns all users ordered by the given column name either ascending or descending
     *
     * @param columnToSortBy the name of the column that will be used to sort the results
     * @param ascending whether to sort by ascending or not
     * @return a users of invoice objects
     */
    private fun <T> selectAll(
        cache: MutableList<T?>?,
        tableName: String,
        resultToT: (ResultSet) -> T,
        columnToSortBy: String = "id",
        ascending: Boolean = true,
    ): MutableList<T> {
        if (columnToSortBy == UserColumns.ID && cache != null) {
            return if (ascending) {
                cache.filterNotNull().toMutableList()
            } else {
                cache.reversed().filterNotNull().toMutableList()
            }
        }

        val sortDirection = if (ascending) {
            "ASC"
        } else {
            "DESC"
        }

        connect().use { connection ->
            connection.prepareStatement("SELECT * FROM $tableName ORDER BY $columnToSortBy $sortDirection")
                .use { preparedStatement ->
                    val results = preparedStatement.executeQuery()
                    val returnList = mutableListOf<T>()
                    while (results.next()) {
                        returnList.add(
                            resultToT(results)
                        )
                    }
                    return returnList
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
        columnToSortBy: String = InvoiceColumns.ID,
        ascending: Boolean = true,
        cache: Boolean = true
    ): List<Invoice> {
        return selectAll(
            if (cache) {
                InvoiceCache
            } else {
                null
            },
            TableNames.INVOICE_TABLE_NAME,
            ::invoiceFromResult,
            columnToSortBy = columnToSortBy,
            ascending = ascending
        )
    }

    /**
     * Returns all users ordered by the given column name either ascending or descending
     *
     * @param columnToSortBy the name of the column that will be used to sort the results
     * @param ascending whether to sort by ascending or not
     * @return a users of invoice objects
     */
    fun selectAllUsers(
        columnToSortBy: String = UserColumns.ID,
        ascending: Boolean = true,
        cache: Boolean = true
    ): List<User> {
        return selectAll(
            if (cache) {
                UserCache
            } else {
                null
            },
            TableNames.USER_TABLE_NAME,
            ::userFromResult,
            columnToSortBy = columnToSortBy,
            ascending = ascending
        )
    }

    /**
     * Returns all clients ordered by the given column name either ascending or descending
     *
     * @param columnToSortBy the name of the column that will be used to sort the results
     * @param ascending whether to sort by ascending or not
     * @return a list of client objects
     */
    fun selectAllClients(
        columnToSortBy: String = ClientColumns.ID,
        ascending: Boolean = true,
        cache: Boolean = true
    ): List<Client> {
        return selectAll(
            if (cache) {
                ClientCache
            } else {
                null
            },
            TableNames.CLIENT_TABLE_NAME,
            ::clientFromResult,
            columnToSortBy = columnToSortBy,
            ascending = ascending
        )
    }

    /**
     * Returns all items ordered by the given column name either ascending or descending
     *
     * @param columnToSortBy the name of the column that will be used to sort the results
     * @param ascending whether to sort by ascending or not
     * @return a list of item objects
     */
    fun selectAllNotes(
        columnToSortBy: String = NoteColumns.ID,
        ascending: Boolean = true,
        cache: Boolean = true
    ): List<String> {
        return selectAll(
            if (cache) {
                NoteCache
            } else {
                null
            },
            TableNames.NOTE_TABLE_NAME,
            ::noteFromResult,
            columnToSortBy = columnToSortBy,
            ascending = ascending
        )
    }

    /**
     * Returns all items ordered by the given column name either ascending or descending
     *
     * @param columnToSortBy the name of the column that will be used to sort the results
     * @param ascending whether to sort by ascending or not
     * @return a list of item objects
     */
    fun selectAllItems(
        columnToSortBy: String = ItemColumns.ID,
        ascending: Boolean = true,
        cache: Boolean = true
    ): List<Item> {
        return selectAll(
            if (cache) {
                ItemCache
            } else {
                null
            },
            TableNames.ITEM_TABLE_NAME,
            ::itemFromResult,
            columnToSortBy = columnToSortBy,
            ascending = ascending
        )
    }

    /**
     * Select a single invoice with a given id
     *
     * @param id
     * @return
     */
    private fun <T> select(id: Int, cache: MutableList<T?>, tableName: String, resultToT: (ResultSet) -> T): T {
        if (cache.size >= id && id >= 1) {
            val cacheHit = cache[id - 1]
            if (cacheHit != null) {
                return cacheHit
            } else {
                throw NoResultException("no row with given id")
            }
        }

        connect().use { connection ->
            connection.prepareStatement(
                "SELECT * FROM $tableName WHERE id = ?"
            ).use { preparedStatement ->
                preparedStatement.setInt(1, id)
                val result = preparedStatement.executeQuery()
                if (!result.next()) throw NoResultException("no row with given id")
                return resultToT(result)
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
        return select(id, UserCache, TableNames.USER_TABLE_NAME, ::userFromResult)
    }

    /**
     * Select a single client with a given id
     *
     * @param id
     * @return
     */
    fun selectClient(id: Int): Client {
        return select(id, ClientCache, TableNames.CLIENT_TABLE_NAME, ::clientFromResult)
    }

    /**
     * Select a single item with a given id
     *
     * @param id
     * @return
     */
    fun selectItem(id: Int): Item {
        return select(id, ItemCache, TableNames.ITEM_TABLE_NAME, ::itemFromResult)
    }

    /**
     * Select a single note with a given id
     *
     * @param id
     * @return
     */
    fun selectNote(id: Int): String {
        return select(id, NoteCache, TableNames.NOTE_TABLE_NAME, ::noteFromResult)
    }

    /**
     * Select a single invoice with a given id
     *
     * @param id
     * @return
     */
    fun selectInvoice(id: Int): Invoice {
        return select(id, InvoiceCache, TableNames.INVOICE_TABLE_NAME, ::invoiceFromResult)
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
        return deleteRowById(TableNames.INVOICE_TABLE_NAME, id, createCacheElementNullifier(ClientCache))
    }

    /**
     * Delete a single user by id
     *
     * @param id
     * @return whether any rows were deleted
     */
    fun deleteUser(id: Int): Boolean {
        return deleteRowById(TableNames.USER_TABLE_NAME, id, createCacheElementNullifier(UserCache))
    }

    /**
     * Delete a single client by id
     *
     * @param id
     * @return whether any rows were deleted
     */
    fun deleteClient(id: Int): Boolean {
        return deleteRowById(TableNames.CLIENT_TABLE_NAME, id, createCacheElementNullifier(ClientCache))
    }

    /**
     * Delete a single client by id
     *
     * @param id
     * @return
     */
    fun deleteItem(id: Int): Boolean {
        return deleteRowById(TableNames.ITEM_TABLE_NAME, id, createCacheElementNullifier(ItemCache))
    }

    /**
     * Delete a single note by id
     *
     * @param id
     * @return
     */
    fun deleteNote(id: Int): Boolean {
        return deleteRowById(TableNames.NOTE_TABLE_NAME, id, createCacheElementNullifier(NoteCache))
    }

    /**
     * Generic search function that
     * searches the clients table for a given value in a given column. The specified query must be a part or a whole of
     * a value in the table. If the value contains a difference from one of the values, it is not a valid match.
     *
     * @param T the data type of the given column
     * @param S the data type of the given query
     * @param columnName the column name to search
     * @param query the query to be searched against the clients table. It is of the generic type given.
     * @return
     */
    private fun <S, T> search(columnName: String, tableName: String, resultToT: (ResultSet) -> T, query: S): List<T> {
        val returnList = mutableListOf<T>()

        connect().use { connection ->
            connection.prepareStatement("SELECT * FROM $tableName WHERE $columnName LIKE ?")
                .use { preparedStatement ->
                    preparedStatement.setString(1, "%$query%")
                    val results = preparedStatement.executeQuery()

                    while (results.next()) {
                        returnList.add(resultToT(results))
                    }
                }
        }

        return returnList
    }

    /**
     * Search the clients table for a given value in a given column. The specified query must be a part or a whole of a
     * value in the table. If the value contains a difference from one of the values, it is not a valid match.
     *
     * @param T the data type of the given column
     * @param columnName the column name to search
     * @param query the query to be searched against the clients table. It is of the generic type given.
     * @return
     */
    fun <T> searchClients(columnName: String, query: T): List<Client> {
        return search(columnName, TableNames.CLIENT_TABLE_NAME, ::clientFromResult, query)
    }

    /**
     * Search the users table for a given value in a given column. The specified query must be a part or a whole of a
     * value in the table. If the value contains a difference from one of the values, it is not a valid match.
     *
     * @param T the data type of the given column
     * @param columnName the column name to search
     * @param query the query to be searched against the users table. It is of the generic type given.
     * @return
     */
    fun <T> searchUsers(columnName: String, query: T): List<User> {
        return search(columnName, TableNames.USER_TABLE_NAME, ::userFromResult, query)
    }

    /**
     * Search the invoices table for a given value in a given column. The specified query must be a part or a whole of a
     * value in the table. If the value contains a difference from one of the values, it is not a valid match.
     *
     * @param T the data type of the given column
     * @param columnName the column name to search
     * @param query the query to be searched against the invoices table. It is of the generic type given.
     * @return
     */
    fun <T> searchInvoices(columnName: String, query: T): List<Invoice> {
        return search(columnName, TableNames.INVOICE_TABLE_NAME, ::invoiceFromResult, query)
    }

    /**
     * Search the items table for a given value in a given column. The specified query must be a part or a whole of a
     * value in the table. If the value contains a difference from one of the values, it is not a valid match.
     *
     * @param T the data type of the given column
     * @param columnName the column name to search
     * @param query the query to be searched against the items table. It is of the generic type given.
     * @return
     */
    fun <T> searchItems(columnName: String, query: T): List<Item> {
        return search(columnName, TableNames.ITEM_TABLE_NAME, ::itemFromResult, query)
    }

    /**
     * Search the notes table for a given value in a given column. The specified query must be a part or a whole of a
     * value in the table. If the value contains a difference from one of the values, it is not a valid match.
     *
     * @param T the data type of the given column
     * @param columnName the column name to search
     * @param query the query to be searched against the notes table. It is of the generic type given.
     * @return
     */
    fun <T> searchNotes(columnName: String, query: T): List<String> {
        return search(columnName, TableNames.NOTE_TABLE_NAME, ::noteFromResult, query)
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
                result.getDate(InvoiceColumns.SEND_DATE),
                result.getString(InvoiceColumns.STATUS),
                result.getString(InvoiceColumns.SENDER),
                result.getString(InvoiceColumns.CLIENT_BUSINESS_NAME),
                result.getString(InvoiceColumns.CLIENT_EMAIL),
                result.getString(InvoiceColumns.CLIENT_PHONE),
                id = result.getInt(InvoiceColumns.ID)
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
                result.getString(UserColumns.BUSINESS_NAME),
                result.getString(UserColumns.CONTACT_NAME),
                result.getString(UserColumns.SUBTITLE),
                result.getString(UserColumns.STREET),
                result.getString(UserColumns.CITY),
                result.getString(UserColumns.STATE),
                result.getInt(UserColumns.ZIP),
                result.getString(UserColumns.EMAIL),
                result.getString(UserColumns.PHONE),
                id = result.getInt(UserColumns.ID)
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
                result.getString(ClientColumns.BUSINESS_NAME),
                result.getString(ClientColumns.CONTACT_NAME),
                result.getString(ClientColumns.STREET),
                result.getString(ClientColumns.CITY),
                result.getString(ClientColumns.STATE),
                result.getInt(ClientColumns.ZIP),
                result.getString(ClientColumns.EMAIL),
                result.getString(ClientColumns.PHONE),
                id = result.getInt(ClientColumns.ID)
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
                result.getString(ItemColumns.NAME),
                result.getDate(ItemColumns.START_DATE),
                result.getDate(ItemColumns.END_DATE),
                result.getDouble(ItemColumns.QUANTITY),
                result.getBigDecimal(ItemColumns.PRICE),
                result.getString(ItemColumns.DESCRIPTION),
                id = result.getInt(ItemColumns.ID)
            )
        } catch (e: SQLException) {
            throw SQLDataRetrievalException("Row Mapping is Incorrect")
        }
    }

    /**
     * Produces a single note object from a ResultSet's current position
     *
     * @param result a JDBC result set
     * @return
     */
    private fun noteFromResult(result: ResultSet): String {
        try {
            return result.getString(NoteColumns.NOTE)
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
    const val INVOICE_TABLE_NAME: String = "Invoices"
    const val USER_TABLE_NAME: String = "Users"
    const val CLIENT_TABLE_NAME: String = "Clients"
    const val ITEM_TABLE_NAME: String = "Items"
    const val NOTE_TABLE_NAME: String = "Notes"
}

/**
 * A consistent place for the names of the invoice table's column names
 *
 * @constructor Create empty Invoice columns
 */
object InvoiceColumns {
    const val SEND_DATE: String = "sendDate"
    const val STATUS: String = "status"
    const val SENDER: String = "sender"
    const val CLIENT_BUSINESS_NAME: String = "clientBusinessName"
    const val CLIENT_EMAIL: String = "clientEmail"
    const val CLIENT_PHONE: String = "clientPhone"
    const val ID: String = "id"
    val columnList: List<String> = listOf(
        SEND_DATE,
        STATUS,
        SENDER,
        CLIENT_BUSINESS_NAME,
        CLIENT_EMAIL,
        CLIENT_PHONE,
        ID
    )
}

/**
 * A consistent place for the names of the user table's column names
 *
 * @constructor Create empty Invoice columns
 */
object UserColumns {
    const val BUSINESS_NAME: String = "businessName"
    const val CONTACT_NAME: String = "contactName"
    const val SUBTITLE: String = "subtitle"
    const val STREET: String = "street"
    const val CITY: String = "city"
    const val STATE: String = "state"
    const val ZIP: String = "zip"
    const val EMAIL: String = "email"
    const val PHONE: String = "phone"
    const val ID: String = "id"
    val columnList: List<String> = listOf(
        BUSINESS_NAME,
        CONTACT_NAME,
        SUBTITLE,
        STREET,
        CITY,
        STATE,
        ZIP,
        EMAIL,
        PHONE,
        ID
    )
}

/**
 * A consistent place for the names of the client table's column names
 *
 * @constructor Create empty Invoice columns
 */
object ClientColumns {
    const val BUSINESS_NAME: String = "businessName"
    const val CONTACT_NAME: String = "contactName"
    const val STREET: String = "street"
    const val CITY: String = "city"
    const val STATE: String = "state"
    const val ZIP: String = "zip"
    const val EMAIL: String = "email"
    const val PHONE: String = "phone"
    const val ID: String = "id"
    val columnList: List<String> = listOf(
        BUSINESS_NAME,
        CONTACT_NAME,
        STREET,
        CITY,
        STATE,
        ZIP,
        EMAIL,
        PHONE,
        ID
    )
}

/**
 * A consistent place for the names of the item table's column names
 *
 * @constructor Create empty Invoice columns
 */
object ItemColumns {
    const val NAME: String = "name"
    const val START_DATE: String = "startDate"
    const val END_DATE: String = "endDate"
    const val QUANTITY: String = "quantity"
    const val PRICE: String = "price"
    const val DESCRIPTION: String = "description"
    const val ID: String = "id"
    val columnList: List<String> = listOf(
        NAME,
        START_DATE,
        END_DATE,
        QUANTITY,
        PRICE,
        DESCRIPTION,
        ID
    )
}

/**
 * A consistent place for the names of the note table's column names
 *
 * @constructor Create empty Invoice columns
 */
object NoteColumns {
    const val NOTE: String = "note"
    const val ID: String = "id"
    val columnList: List<String> = listOf(
        NOTE,
        ID
    )
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

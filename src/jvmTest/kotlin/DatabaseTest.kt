import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import storage.*
import java.io.File
import java.sql.Date
import java.sql.DriverManager
import kotlinx.datetime.*
import java.sql.Connection
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


internal class DatabaseTest {

    private fun insertFakeData(connection: Connection) {
        insertInvoice(
            connection, Invoice(
                Date(System.currentTimeMillis()),
                "status",
                "sender",
                "clientBusinessName",
                "clientEmail",
                "clientPhone"
            )
        )
        insertUser(
            connection, User(
                "businessName", "contactName", "subtitle", "street", "city", "state", 11111, "email", "phone"
            )
        )
        insertClient(
            connection, Client(
                "businessName", "contactName", "street", "city", "state", 11111, "email", "phone"
            )
        )
        insertItem(
            connection, Item(
                "name",
                Date(System.currentTimeMillis()),
                Date(System.currentTimeMillis()),
                1.5,
                1.5.toBigDecimal(),
                "description"
            )
        )
    }

    private fun deleteDatabase(databaseFilePath: String) {
        val databaseFile = File(databaseFilePath)
        if (databaseFile.exists()) {
            databaseFile.delete()
        }
    }

    @Test
    fun testConnect() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            assertTrue(doesTableExist(connection, TableNames.clientTableName))
            try {
                createTables(connection)
            } catch (e: Exception) {
                throw AssertionError("createTables() threw an exception")
            }
            assertTrue(doesTableExist(connection, TableNames.clientTableName))
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testCreateTables() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        DriverManager.getConnection("jdbc:sqlite:$databaseFilePath").use { connection ->
            createTables(connection)
            assertTrue(doesTableExist(connection, TableNames.invoiceTableName))
            assertTrue(doesTableExist(connection, TableNames.userTableName))
            assertTrue(doesTableExist(connection, TableNames.clientTableName))
            assertTrue(doesTableExist(connection, TableNames.itemTableName))
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testNonExistentTable() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        DriverManager.getConnection("jdbc:sqlite:$databaseFilePath").use { connection ->
            assertFalse(doesTableExist(connection, TableNames.invoiceTableName))
            assertFalse(doesTableExist(connection, TableNames.userTableName))
            assertFalse(doesTableExist(connection, TableNames.clientTableName))
            assertFalse(doesTableExist(connection, TableNames.itemTableName))
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testInsert() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            insertFakeData(connection)

            assertEquals(selectAllInvoices(connection, InvoiceColumns.id)[0].status, "status")
            assertEquals(selectAllClients(connection, ClientColumns.id)[0].businessName, "businessName")
            assertEquals(selectAllUsers(connection, UserColumns.id)[0].businessName, "businessName")
            assertEquals(selectAllItems(connection, ItemColumns.id, true)[0].description, "description")
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testInsertAndSelect() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            val result: String = selectClient(
                connection, insertClient(
                    connection, Client(
                        "businessName", "contactName", "street", "city", "state", 11111, "email", "phone"
                    )
                )
            ).city

            assertEquals(result, "city")
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testInsertAndDelete() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            val result = deleteClient(
                connection, insertClient(
                    connection, Client(
                        "businessName", "contactName", "street", "city", "state", 11111, "email", "phone"
                    )
                )
            )
            assertTrue(result)
            assertFailsWith<NoResultException> {
                selectClient(connection, 1)
            }
            assertFalse(deleteClient(connection, 1))
            assertFalse(deleteClient(connection, 2))
        }
        deleteDatabase(databaseFilePath)
    }

//    val databaseFilePath = "${::testSelectAll.name}.db"
//    connect(databaseFilePath).use { connection ->
//
//    }
//    deleteDatabase(databaseFilePath)

    @Test
    fun testSelectAll() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            for (i in 1..5) {
                insertClient(
                    connection, Client(
                        "$i", "contactName", "street", "city", "state", 11111, "email", "phone"
                    )
                )
            }
            var i = 1
            for (client: Client in selectAllClients(connection, ClientColumns.businessName)) {
                assertEquals("${i++}", client.businessName)
            }
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testSelectAllSortByString() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            val names = listOf("Audrey", "Bob", "Jeff", "Melissa")

            insertClient(
                connection, Client(
                    "businessName", names[1], "street", "city", "state", 11111, "email", "phone"
                )
            )
            insertClient(
                connection, Client(
                    "businessName", names[2], "street", "city", "state", 11111, "email", "phone"
                )
            )
            insertClient(
                connection, Client(
                    "businessName", names[0], "street", "city", "state", 11111, "email", "phone"
                )
            )
            insertClient(
                connection, Client(
                    "businessName", names[3], "street", "city", "state", 11111, "email", "phone"
                )
            )

            var i = 0
            for (client in selectAllClients(connection, ClientColumns.contactName)) {
                assertEquals(names[i++], client.contactName)
            }
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testSelectAllSortByDateDescending() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            val times = listOf<Long>(
                Instant.parse(createIso8601String(2023, 8, 14)).toEpochMilliseconds(),
                Instant.parse(createIso8601String(2022, 8, 14)).toEpochMilliseconds(),
                Instant.parse(createIso8601String(2021, 8, 14)).toEpochMilliseconds(),
                Instant.parse(createIso8601String(2020, 8, 14)).toEpochMilliseconds()
            )

            insertItem(
                connection, Item(
                    "name", Date(times[1]), Date(System.currentTimeMillis()), 1.5, 1.5.toBigDecimal(), "description"
                )
            )

            insertItem(
                connection, Item(
                    "name", Date(times[3]), Date(System.currentTimeMillis()), 1.5, 1.5.toBigDecimal(), "description"
                )
            )

            insertItem(
                connection, Item(
                    "name", Date(times[0]), Date(System.currentTimeMillis()), 1.5, 1.5.toBigDecimal(), "description"
                )
            )

            insertItem(
                connection, Item(
                    "name", Date(times[2]), Date(System.currentTimeMillis()), 1.5, 1.5.toBigDecimal(), "description"
                )
            )

            var i = 0
            for (item in selectAllItems(connection, ItemColumns.startDate, false)) {
                assertEquals(Date(times[i++]), item.startDate)
            }
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testSelectAllNoRows() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            assertEquals(listOf<Client>(),selectAllClients(connection))
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testSelectRow() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            insertFakeData(connection)
            assertEquals("businessName",selectClient(connection, 1).businessName)
            assertEquals(selectAllClients(connection)[0].businessName, selectClient(connection, 1).businessName)
            assertEquals("businessName",selectUser(connection, 1).businessName)
            assertEquals(selectAllUsers(connection)[0].businessName, selectUser(connection, 1).businessName)
            assertEquals("sender",selectInvoice(connection, 1).sender)
            assertEquals(selectAllInvoices(connection)[0].sender, selectInvoice(connection, 1).sender)
            assertEquals("description",selectItem(connection, 1).description)
            assertEquals(selectAllItems(connection)[0].description, selectItem(connection, 1).description)
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testSelectNoRow() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            assertFailsWith<NoResultException> {
                selectClient(connection, 1)
            }
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testDelete() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            for(i in 1..3) {
                insertFakeData(connection)
            }
            deleteClient(connection, 2)
            assertFailsWith<NoResultException> {
                selectClient(connection, 2)
            }
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testSearch() {

    }
}

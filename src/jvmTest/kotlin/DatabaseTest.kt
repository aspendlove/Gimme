
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import storage.*
import java.io.File
import java.sql.Date
import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


internal class DatabaseTest {

    private fun deleteDatabase(databaseFilePath: String) {
        val databaseFile = File(databaseFilePath)
        if (databaseFile.exists()) {
            databaseFile.delete()
        }
    }

//    val databaseFilePath: String = "${::testSelectAll.name}.db"
//    connect(databaseFilePath).use { connection ->
//
//    }
//    deleteDatabase(databaseFilePath)

    @Test
    fun testConnect() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use {connection ->
            assertTrue(doesTableExist(connection, TableNames.clientTableName))
            try {
                createTables(connection)
            } catch(e: Exception) {
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
                    "businessName",
                    "contactName",
                    "subtitle",
                    "street",
                    "city",
                    "state",
                    11111,
                    "email",
                    "phone"
                )
            )
            insertClient(
                connection,
                Client(
                    "businessName",
                    "contactName",
                    "street",
                    "city",
                    "state",
                    11111,
                    "email",
                    "phone"
                )
            )
            insertItem(
                connection,
                Item(
                    "name",
                    Date(System.currentTimeMillis()),
                    Date(System.currentTimeMillis()),
                    1.5,
                    1.5.toBigDecimal(),
                    "description"
                )
            )

            assertEquals(selectAllInvoices(connection, InvoiceColumns.id)[0].status, "status")
            assertEquals(selectAllClients(connection, ClientColumns.id)[0].businessName, "businessName")
            assertEquals(selectAllUsers(connection, UserColumns.id)[0].businessName, "businessName")
            assertEquals(selectAllItems(connection, ItemColumns.id)[0].description, "description")
        }
        deleteDatabase(databaseFilePath)
    }

    @Test
    fun testInsertAndSelect() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            val result: String = selectClient(
                connection,
                insertClient(
                    connection,
                    Client(
                        "businessName",
                        "contactName",
                        "street",
                        "city",
                        "state",
                        11111,
                        "email",
                        "phone"
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
                connection,
                insertClient(
                    connection,
                    Client(
                        "businessName",
                        "contactName",
                        "street",
                        "city",
                        "state",
                        11111,
                        "email",
                        "phone"
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
            for(i in 1..5) {
                insertClient(
                    connection,
                    Client(
                        "$i",
                        "contactName",
                        "street",
                        "city",
                        "state",
                        11111,
                        "email",
                        "phone"
                    )
                )
            }
            var i = 1
            for(client: Client in selectAllClients(connection, ClientColumns.businessName)) {
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
                connection,
                Client(
                    "businessName",
                    names[1],
                    "street",
                    "city",
                    "state",
                    11111,
                    "email",
                    "phone"
                )
            )
            insertClient(
                connection,
                Client(
                    "businessName",
                    names[2],
                    "street",
                    "city",
                    "state",
                    11111,
                    "email",
                    "phone"
                )
            )
            insertClient(
                connection,
                Client(
                    "businessName",
                    names[0],
                    "street",
                    "city",
                    "state",
                    11111,
                    "email",
                    "phone"
                )
            )
            insertClient(
                connection,
                Client(
                    "businessName",
                    names[3],
                    "street",
                    "city",
                    "state",
                    11111,
                    "email",
                    "phone"
                )
            )

            var i = 0
            for(client in selectAllClients(connection, ClientColumns.contactName)) {
                assertEquals(names[i++], client.contactName)
            }
        }
        deleteDatabase(databaseFilePath)
    }
    fun testSelectAllSortByDate() {
        val databaseFilePath = "${::testSelectAll.name}.db"
        connect(databaseFilePath).use { connection ->
            val names = listOf("Audrey", "Bob", "Jeff", "Melissa")

            val humanReadableDate = "2023-08-15" // Example date

            val dateTime = LocalDateTime.parse(humanReadableDate, java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy"))
            val millisecondsSinceEpoch = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli()

            println("Milliseconds since Unix epoch: $millisecondsSinceEpoch")

            insertItem(
                connection,
                Item(
                    "name",
                    Date(millisecondsSinceEpoch),
                    Date(System.currentTimeMillis()),
                    1.5,
                    1.5.toBigDecimal(),
                    "description"
                )
            )

            var i = 0
            for(client in selectAllClients(connection, ClientColumns.contactName)) {
                assertEquals(names[i++], client.contactName)
            }
        }
        deleteDatabase(databaseFilePath)
    }
}

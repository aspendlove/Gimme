
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import storage.*
import java.sql.Date
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


internal class DatabaseSingletonTest {

    init {
        DatabaseManager.databaseFilePath = this::class.simpleName.toString() + ".db"
    }

    private fun insertFakeData() {
        DatabaseManager.insertInvoice(
            Invoice(
                Date(System.currentTimeMillis()),
                "status",
                "sender",
                "clientBusinessName",
                "clientEmail",
                "clientPhone"
            )
        )
        DatabaseManager.insertUser(
            User(
                "businessName", "contactName", "subtitle", "street", "city", "state", 11111, "email", "phone"
            )
        )
        DatabaseManager.insertClient(
            Client(
                "businessName", "contactName", "street", "city", "state", 11111, "email", "phone"
            )
        )
        DatabaseManager.insertItem(
            Item(
                "name",
                Date(System.currentTimeMillis()),
                Date(System.currentTimeMillis()),
                1.5,
                1.5.toBigDecimal(),
                "description"
            )
        )
    }

//    private fun deleteDatabase(databaseFilePath: String) {
//        val databaseFile = File(databaseFilePath)
//        if (databaseFile.exists()) {
//            databaseFile.delete()
//        }
//    }

//    @Test
//    fun testConnect() {
//        val databaseFilePath = "${::testSelectAll.name}.db"
//        assertTrue(DatabaseManager.doesTableExist(connection, TableNames.clientTableName))
//        try {
//            createTables(connection)
//        } catch (e: Exception) {
//            throw AssertionError("createTables() threw an exception")
//        }
//        assertTrue(doesTableExist(connection, TableNames.clientTableName))
//        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
//    }

//    @Test
//    fun testCreateTables() {
//        val databaseFilePath = "${::testSelectAll.name}.db"
//        DriverManager.getConnection("jdbc:sqlite:$databaseFilePath").use { connection ->
//            createTables(connection)
//            assertTrue(doesTableExist(connection, TableNames.invoiceTableName))
//            assertTrue(doesTableExist(connection, TableNames.userTableName))
//            assertTrue(doesTableExist(connection, TableNames.clientTableName))
//            assertTrue(doesTableExist(connection, TableNames.itemTableName))
//        }
//        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
//    }

//    @Test
//    fun testNonExistentTable() {
//        val databaseFilePath = "${::testSelectAll.name}.db"
//        DriverManager.getConnection("jdbc:sqlite:$databaseFilePath").use { connection ->
//            assertFalse(doesTableExist(connection, TableNames.invoiceTableName))
//            assertFalse(doesTableExist(connection, TableNames.userTableName))
//            assertFalse(doesTableExist(connection, TableNames.clientTableName))
//            assertFalse(doesTableExist(connection, TableNames.itemTableName))
//        }
//        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
//    }

    @Test
    fun testInsert() {
        insertFakeData()
        assertEquals(
            DatabaseManager.selectAllInvoices(InvoiceColumns.id)[0].status,
            "status"
        )
        assertEquals(DatabaseManager.selectAllClients(ClientColumns.id)[0].businessName,
            "businessName")
        assertEquals(DatabaseManager.selectAllUsers(UserColumns.id)[0].businessName,
            "businessName")
        assertEquals(DatabaseManager.selectAllItems(ItemColumns.id, true)[0].description,
            "description")
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }

    @Test
    fun testInsertAndSelect() {
        with(DatabaseManager) {
            val result: String = selectClient(
                insertClient(
                    Client(
                        "businessName", "contactName", "street", "city", "state", 11111, "email", "phone"
                    )
                )
            ).city

            assertEquals(result, "city")
        }
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }

    @Test
    fun testInsertAndDelete() {
        with(DatabaseManager) {
            val result = deleteClient(
                insertClient(
                    Client(
                        "businessName", "contactName", "street", "city", "state", 11111, "email", "phone"
                    )
                )
            )
            assertTrue(result)
            assertFailsWith<NoResultException> {
                selectClient(2)
            }
            insertClient(
                Client(
                    "businessName", "contactName", "street", "city", "state", 11111, "email", "phone"
                )
            )
            assertTrue(deleteClient(1))
            assertFalse(deleteClient(2))
        }
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }

    @Test
    fun testSelectAll() {
        with(DatabaseManager) {
            for (i in 1..5) {
                insertClient(
                    Client(
                        "$i", "contactName", "street", "city", "state", 11111, "email", "phone"
                    )
                )
            }
            var i = 1
            for (client: Client in selectAllClients(ClientColumns.businessName)) {
                assertEquals("${i++}", client.businessName)
            }
        }
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }

    @Test
    fun testSelectAllSortByString() {
        with (DatabaseManager) {
            val names = listOf("Audrey", "Bob", "Jeff", "Melissa")

            insertClient(
                Client(
                    "businessName", names[1], "street", "city", "state", 11111, "email", "phone"
                )
            )
            insertClient(
                Client(
                    "businessName", names[2], "street", "city", "state", 11111, "email", "phone"
                )
            )
            insertClient(
                Client(
                    "businessName", names[0], "street", "city", "state", 11111, "email", "phone"
                )
            )
            insertClient(
                Client(
                    "businessName", names[3], "street", "city", "state", 11111, "email", "phone"
                )
            )

            var i = 0
            for (client in selectAllClients(ClientColumns.contactName)) {
                assertEquals(names[i++], client.contactName)
            }
        }
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }

    @Test
    fun testSelectAllSortByDateDescending() {
        with (DatabaseManager) {
            val times = listOf<Long>(
//                Instant.parse(createIso8601String(2023, 9, 21)).toEpochMilliseconds(),
//                Instant.parse(createIso8601String(2022, 9, 21)).toEpochMilliseconds(),
//                Instant.parse(createIso8601String(2021, 9, 21)).toEpochMilliseconds(),
//                Instant.parse(createIso8601String(2020, 9, 21)).toEpochMilliseconds()
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                System.currentTimeMillis()
            )

            insertItem(
                Item(
                    "name", Date(times[1]), Date(times[1]), 1.5, 1.5.toBigDecimal(), "description"
                )
            )

            insertItem(
                Item(
                    "name", Date(times[3]), Date(times[3]), 1.5, 1.5.toBigDecimal(), "description"
                )
            )

            insertItem(
                Item(
                    "name", Date(times[0]), Date(times[0]), 1.5, 1.5.toBigDecimal(), "description"
                )
            )

            insertItem(
                Item(
                    "name", Date(times[2]), Date(times[2]), 1.5, 1.5.toBigDecimal(), "description"
                )
            )

            var i = 0
            for (item in selectAllItems(ItemColumns.startDate, false)) {
                assertEquals(Date(times[i++]), item.startDate)
            }
        }
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }

    @Test
    fun testSelectAllNoRows() {
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
        with (DatabaseManager) {
            assertEquals(listOf<Client>(), selectAllClients())
        }
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }

    @Test
    fun testSelectRow() {
        with (DatabaseManager) {
            insertFakeData()
            assertEquals("businessName", selectClient(1).businessName)
            assertEquals(selectAllClients()[0].businessName, selectClient(1).businessName)
            assertEquals("businessName", selectUser(1).businessName)
            assertEquals(selectAllUsers()[0].businessName, selectUser(1).businessName)
            assertEquals("sender", selectInvoice(1).sender)
            assertEquals(selectAllInvoices()[0].sender, selectInvoice(1).sender)
            assertEquals("description", selectItem(1).description)
            assertEquals(selectAllItems()[0].description, selectItem(1).description)
        }
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }

    @Test
    fun testSelectNoRow() {
        with (DatabaseManager) {
            assertFailsWith<NoResultException> {
                selectClient(1)
            }
        }
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }

    @Test
    fun testDelete() {
        with (DatabaseManager) {
            for (i in 1..3) {
                insertFakeData()
            }
            deleteClient(2)
            assertFailsWith<NoResultException> {
                selectClient(2)
            }
        }
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }

    @Test
    fun testSearch() {
        with (DatabaseManager) {
            insertFakeData()
            val fullNameClient: Client =
                searchClients<String>(ClientColumns.businessName, "businessName")[0]
            val partNameUser: User = searchUsers<String>(UserColumns.businessName, "iness")[0]
            val wrongNameItem: List<Item> = searchItems<String>(ItemColumns.description, "bad query")

            assertEquals("businessName", fullNameClient.businessName)
            assertEquals("businessName", partNameUser.businessName)
            assertTrue(wrongNameItem.isEmpty())
        }
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }

    //    val databaseFilePath = "${::testSelectAll.name}.db"
    //    connect(databaseFilePath).use { connection ->
    //
    //    }
    //    deleteDatabase(databaseFilePath)
}

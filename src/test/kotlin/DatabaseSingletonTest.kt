
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

    @Test
    fun testInsert() {
        insertFakeData()
        assertEquals(
            DatabaseManager.selectAllInvoices(InvoiceColumns.ID)[0].status,
            "status"
        )
        assertEquals(DatabaseManager.selectAllClients(ClientColumns.ID)[0].businessName,
            "businessName")
        assertEquals(DatabaseManager.selectAllUsers(UserColumns.ID)[0].businessName,
            "businessName")
        assertEquals(DatabaseManager.selectAllItems(ItemColumns.ID, true)[0].description,
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
            for (client: Client in selectAllClients(ClientColumns.BUSINESS_NAME)) {
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
            for (client in selectAllClients(ClientColumns.CONTACT_NAME)) {
                assertEquals(names[i++], client.contactName)
            }
        }
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }

    @Test
    fun testSelectAllSortByDateDescending() {
        with (DatabaseManager) {
            val times = listOf(
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
            for (item in selectAllItems(ItemColumns.START_DATE, false)) {
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
                searchClients(ClientColumns.BUSINESS_NAME, "businessName")[0]
            val partNameUser: User = searchUsers(UserColumns.BUSINESS_NAME, "iness")[0]
            val wrongNameItem: List<Item> = searchItems(ItemColumns.DESCRIPTION, "bad query")

            assertEquals("businessName", fullNameClient.businessName)
            assertEquals("businessName", partNameUser.businessName)
            assertTrue(wrongNameItem.isEmpty())
        }
        DatabaseManager.wipeDatabase("DELETE THE DATABASE")
    }
}

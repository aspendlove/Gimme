
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Test
import storage.*
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class DatabaseTest {

    private val exampleBusinessName = "Business Name"
    private val exampleContactName = "Contact Name"
    private val exampleSubtitle = "Subtitle"
    private val exampleStreet = "Street"
    private val exampleCity = "City"
    private val exampleState = "State"
    private val exampleZip = 11111
    private val exampleEmail = "email@email.com"
    private val examplePhone = "111-111-1111"
    private val databaseFilename = "testData.db"
    private val exampleStartDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    private val exampleEndDate = exampleStartDate
    private val exampleSendDate = exampleStartDate
    private val exampleStatus = "Status"
    private val exampleItemName = "Item Name"
    private val exampleDescription = "Description"
    private val exampleQuantity = 1.0
    private val examplePrice = 1.0

    @Test
    fun testCreate() {
        val file = File(databaseFilename)
        file.delete()
        initDatabase(databaseFilename)
        assertTrue { file.exists() }
    }

    @Test
    fun testInsertAndReadClient() {

        val client = insertClient(
            "testData.db",
            exampleBusinessName,
            exampleContactName,
            exampleStreet,
            exampleCity,
            exampleState,
            exampleZip,
            exampleEmail,
            examplePhone
        )

        assertEquals(exampleBusinessName, client.businessName)
        assertEquals(exampleContactName, client.contactName)
        assertEquals(exampleStreet, client.street)
        assertEquals(exampleCity, client.city)
        assertEquals(exampleState, client.state)
        assertEquals(exampleZip, client.zip)
        assertEquals(exampleEmail, client.email)
        assertEquals(examplePhone, client.phone)

        val selectedClient = selectClientById(databaseFilename, client.id.value)

        assertEquals(exampleBusinessName, selectedClient.businessName)
        assertEquals(exampleContactName, selectedClient.contactName)
        assertEquals(exampleStreet, selectedClient.street)
        assertEquals(exampleCity, selectedClient.city)
        assertEquals(exampleState, selectedClient.state)
        assertEquals(exampleZip, selectedClient.zip)
        assertEquals(exampleEmail, selectedClient.email)
        assertEquals(examplePhone, selectedClient.phone)
    }

    @Test
    fun testInsertAndReadUser() {

        val user = insertUser(
            "testData.db",
            exampleBusinessName,
            exampleContactName,
            exampleSubtitle,
            exampleStreet,
            exampleCity,
            exampleState,
            exampleZip,
            exampleEmail,
            examplePhone
        )

        assertEquals(exampleBusinessName, user.businessName)
        assertEquals(exampleContactName, user.contactName)
        assertEquals(exampleSubtitle, user.subtitle)
        assertEquals(exampleStreet, user.street)
        assertEquals(exampleCity, user.city)
        assertEquals(exampleState, user.state)
        assertEquals(exampleZip, user.zip)
        assertEquals(exampleEmail, user.email)
        assertEquals(examplePhone, user.phone)

        val selectedUser = selectUserById(databaseFilename, user.id.value)

        assertEquals(exampleBusinessName, selectedUser.businessName)
        assertEquals(exampleContactName, selectedUser.contactName)
        assertEquals(exampleSubtitle, user.subtitle)
        assertEquals(exampleStreet, selectedUser.street)
        assertEquals(exampleCity, selectedUser.city)
        assertEquals(exampleState, selectedUser.state)
        assertEquals(exampleZip, selectedUser.zip)
        assertEquals(exampleEmail, selectedUser.email)
        assertEquals(examplePhone, selectedUser.phone)
    }

    @Test
    fun testAndInsertItem() {
        val item = insertItem(
            databaseFilename,
            exampleStartDate,
            exampleEndDate,
            exampleItemName,
            exampleDescription,
            exampleQuantity,
            examplePrice
        )

        assertEquals(exampleStartDate, item.startDate)
        assertEquals(exampleEndDate, item.endDate)
        assertEquals(exampleItemName, item.name)
        assertEquals(exampleDescription, item.description)
        assertEquals(exampleQuantity, item.quantity)
        assertEquals(examplePrice, item.price)

        val selectedItem = selectItemById(databaseFilename, item.id.value)

        assertEquals(exampleStartDate, selectedItem.startDate)
        assertEquals(exampleEndDate, selectedItem.endDate)
        assertEquals(exampleItemName, selectedItem.name)
        assertEquals(exampleDescription, selectedItem.description)
        assertEquals(exampleQuantity, selectedItem.quantity)
        assertEquals(examplePrice, selectedItem.price)
    }

    @Test
    fun testAndInsertInvoice() {
        val invoice = insertInvoice(
            databaseFilename,
            exampleStartDate,
            exampleStatus,
            exampleBusinessName,
            exampleBusinessName,
            exampleEmail,
            examplePhone
        )

        assertEquals(exampleStartDate, invoice.sendDate)
        assertEquals(exampleStatus, invoice.status)
        assertEquals(exampleBusinessName, invoice.sender)
        assertEquals(exampleBusinessName, invoice.clientBusinessName)
        assertEquals(exampleEmail, invoice.clientEmail)
        assertEquals(examplePhone, invoice.clientPhone)

        val selectedInvoice = selectInvoiceById(databaseFilename, invoice.id.value)

        assertEquals(exampleStartDate, selectedInvoice.sendDate)
        assertEquals(exampleStatus, selectedInvoice.status)
        assertEquals(exampleBusinessName, selectedInvoice.sender)
        assertEquals(exampleBusinessName, selectedInvoice.clientBusinessName)
        assertEquals(exampleEmail, selectedInvoice.clientEmail)
        assertEquals(examplePhone, selectedInvoice.clientPhone)
    }
}

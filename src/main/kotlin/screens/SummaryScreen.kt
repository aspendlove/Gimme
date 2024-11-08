package screens

import InvoiceBuilder
import RegexBuilder
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.Body
import components.CustomButton
import kotlinx.coroutines.runBlocking
import storage.DatabaseManager
import storage.Invoice
import storage.StateBundle
import java.awt.FileDialog
import java.awt.Frame
import java.sql.Date
import java.text.SimpleDateFormat

class SummaryScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val userText = with(StateBundle.user) {
            Body(
                """
                User Details:
                Business: $businessName
                Name: $contactName
                Subtitle: $subtitle
                Address:
                    $street
                    $city, $state $zip
                Email: $email
                Phone: $phone
                """.trimIndent()
            )
        }
        val clientText = with(StateBundle.client) {
            Body(
                """
                Client Details:
                Business: $businessName
                Name: $contactName
                Address:
                    $street
                    $city, $state $zip
                Email: $email
                Phone: $phone
                """.trimIndent()
            )
        }
        var formattedItems = "Services:\n"
        val dateFormat = SimpleDateFormat("MM/dd/yyyy")
        for (item in StateBundle.items) {
            formattedItems += with(item) {
                """Name: $name, Date: ${dateFormat.format(startDate)}${
                    if (endDate != null) {
                        " - ${dateFormat.format(endDate)}"
                    } else {
                        ""
                    }
                }, Price: $quantity x $$price, Description: $description""" + "\n"
            }
        }
        val itemText = Body(formattedItems)
        val notesText = Body("Notes:\n" + StateBundle.notes.note)
        val forwardButton = CustomButton({
            val completedInvoice = with(StateBundle) {
                Invoice(
                    Date(System.currentTimeMillis()),
                    "unpaid",
                    user.businessName,
                    client.businessName,
                    client.email,
                    client.phone,
                )
            }

            val invoiceId = DatabaseManager.insertInvoice(completedInvoice)

            val invoiceName = Invoice.formatInvoiceName(invoiceId)

            val dialog = FileDialog(null as Frame?, "Save File")
            dialog.mode = FileDialog.SAVE
            dialog.file = "$invoiceName.html"

            dialog.isVisible = true
            if (dialog.directory == null || dialog.file == null) {
                DatabaseManager.deleteInvoice(invoiceId)
                return@CustomButton
            }
            val fullPath: String = dialog.directory + dialog.file
            val invoiceBuilder: InvoiceBuilder = RegexBuilder()
            runBlocking {
                invoiceBuilder.build(fullPath, invoiceName)
            }
            navigator += ConclusionScreen()
        }, "Create Invoice")
        val backButton = CustomButton({
            navigator.pop()
        }, "Back")

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val cardModifier = Modifier.fillMaxWidth().weight(1f).padding(10.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(modifier = cardModifier) {
                    userText.compose()
                }
                Card(modifier = cardModifier) {
                    clientText.compose()
                }
            }
            Card(modifier = cardModifier) {
                itemText.compose()
            }
            Card(modifier = cardModifier) {
                notesText.compose()
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                backButton.compose()
                forwardButton.compose()
            }
        }
    }
}

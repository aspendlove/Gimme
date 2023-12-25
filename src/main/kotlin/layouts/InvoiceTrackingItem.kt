package layouts

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import components.Body
import components.CustomButton
import storage.Invoice

class InvoiceTrackingItem(
    val id: Int,
    private val item: Invoice
) {
    @Composable
    fun compose() {
        val invoiceString = with(item) {
            "Date: $sendDate, Status: $status, Sender: $sender, Client: $clientBusinessName, Phone: $clientPhone, Email: $clientEmail"
        }

        Row {
            Body(invoiceString).compose()
        }
    }
}

package layouts


import RequiredTextMultiline
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import components.NonRequiredText
import components.RequiredText
import storage.Item
import java.sql.Date

class ItemInputRowDialog(val id: Int) {

    private val nameInput = RequiredText("Service")
    private val startDateInput = RequiredText("Start Date")
    private val endDateInput = NonRequiredText("End Date")
    private val descriptionInput = RequiredTextMultiline("Description")
    private val quantityInput = RequiredText("Quantity")
    private val priceInput = RequiredText("Price")

    val isError: Boolean
        get() {
            return nameInput.isError ||
                    startDateInput.isError ||
                    descriptionInput.isError ||
                    quantityInput.isError ||
                    priceInput.isError
        }

    val result: Item
        get() {
            return Item(
                nameInput.result,
                Date(10), // TODO convert string to date
                Date(11),
                quantityInput.result.toDouble(),
                priceInput.result.toBigDecimal(),
                descriptionInput.result
            )
        }

//    val results: Map<String,String>
//        get() {
//            return mapOf(
//                "Name" to nameInput.result,
//                "StartDate" to startDateInput.result,
//                "EndDate" to endDateInput.result,
//                "Quantity" to quantityInput.result,
//                "Price" to priceInput.result,
//                "Description" to descriptionInput.result
//            )
//        }

    @Composable
    fun compose() {
        Row {
            Box(modifier = Modifier.weight(1f)) {
                nameInput.compose()
            }
            Box(modifier = Modifier.weight(1f)) {
                startDateInput.compose()
            }
            Box(modifier = Modifier.weight(1f)) {
                endDateInput.compose()
            }
            Box(modifier = Modifier.weight(1f)) {
                quantityInput.compose()
            }
            Box(modifier = Modifier.weight(1f)) {
                priceInput.compose()
            }
            Box(modifier = Modifier.weight(2f)) {
                descriptionInput.compose()
            }
        }
    }
}

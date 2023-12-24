package layouts


import components.RequiredTextMultiline
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import components.CustomComponentBase
import components.DateEntry
import components.RequiredText
import compose.icons.FeatherIcons
import compose.icons.feathericons.FileText
import compose.icons.feathericons.Save
import storage.Item

class ItemInputRowDialog(val id: Int) : CustomComponentBase(_modifier = Modifier) {

    private val nameInput = RequiredText("Service")
    private val startDateInput = DateEntry("Start Date")
    private val endDateInput = DateEntry("End Date", required = false)
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
                startDateInput.result!!,
                endDateInput.result,
                quantityInput.result.toDouble(),
                priceInput.result.toBigDecimal(),
                descriptionInput.result
            )
        }

    @Composable
    override fun compose() {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            Button(
                onClick = {
                    TODO("Implement Loading items")
                },
                modifier = Modifier.fillMaxHeight()
            ) {
                Icon(FeatherIcons.FileText, "Load")
            }
            Button(
                onClick = {
                    TODO("Implement Saving items")
                },
                modifier = Modifier.fillMaxHeight()
            ) {
                Icon(FeatherIcons.Save, "Save")
            }
        }
    }
}

package layouts


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
import components.RequiredTextMultiline
import compose.icons.FeatherIcons
import compose.icons.feathericons.Save
import storage.DatabaseManager
import storage.Item

class ItemInputRowDialog(val id: Int, val onSave: () -> Unit, val item: Item? = null) : CustomComponentBase(_modifier = Modifier) {
    private val nameInput: RequiredText
    private val startDateInput: DateEntry
    private val endDateInput: DateEntry
    private val descriptionInput: RequiredTextMultiline
    private val quantityInput: RequiredText
    private val priceInput: RequiredText
    init {
        if(item != null) {
            with(item) {
                nameInput = RequiredText("Service",name)
                startDateInput = DateEntry("Start Date", millisSinceEpoch = startDate.time)
                endDateInput = DateEntry("End Date", required = false, millisSinceEpoch = endDate?.time)
                descriptionInput = RequiredTextMultiline("Description", description)
                quantityInput = RequiredText("Quantity", quantity.toString())
                priceInput = RequiredText("Price", price.toString())
            }
        } else {
            nameInput = RequiredText("Service")
            startDateInput = DateEntry("Start Date")
            endDateInput = DateEntry("End Date", required = false)
            descriptionInput = RequiredTextMultiline("Description")
            quantityInput = RequiredText("Quantity")
            priceInput = RequiredText("Price")
        }
    }

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
                if(quantityInput.result.isEmpty()) 0.0 else quantityInput.result.toDouble(),
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
                    DatabaseManager.insertItem(result)
                    onSave()
                },
                modifier = Modifier.fillMaxHeight()
            ) {
                Icon(FeatherIcons.Save, "Save")
            }
        }
    }
}

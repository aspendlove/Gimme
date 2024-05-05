package layouts


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import components.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.Save
import storage.DatabaseManager
import storage.Item
import java.math.BigDecimal

// TODO change into a function that has callbacks on value change and error change. The value should be updated only when there is a full item
class ItemInputRowDialog(val id: Int, val onSave: () -> Unit, val item: Item? = null) :
    ComponentBase(_modifier = Modifier) {

    private val nameResult: ValueErrorPair<String>
    private val startDateResult: ValueErrorPair<java.sql.Date?>
    private val endDateResult: ValueErrorPair<java.sql.Date?>
    private val descriptionResult: ValueErrorPair<String>
    private val quantityResult: ValueErrorPair<Double>
    private val priceResult: ValueErrorPair<BigDecimal>

    init {
        if (item != null) {
            with(item) {
                nameResult = ValueErrorPair(name, true)
                startDateResult = ValueErrorPair(startDate, true)
                endDateResult = ValueErrorPair(endDate, true)
                quantityResult = ValueErrorPair(quantity, true)
                priceResult = ValueErrorPair(price, true)
                descriptionResult = ValueErrorPair(description, true)
            }
        } else {
            nameResult = ValueErrorPair("", true)
            startDateResult = ValueErrorPair(null, true)
            endDateResult = ValueErrorPair(null, true)
            descriptionResult = ValueErrorPair("", true)
            quantityResult = ValueErrorPair(0.0, true)
            priceResult = ValueErrorPair(0.0.toBigDecimal(), true)
        }
    }

    val isError: Boolean
        get() {
            return nameResult.error ||
                    startDateResult.error ||
                    descriptionResult.error ||
                    quantityResult.error ||
                    priceResult.error
        }

    val result: Item
        get() {
            return Item(
                nameResult.value,
                startDateResult.value!!,
                endDateResult.value,
                quantityResult.value,
                priceResult.value,
                descriptionResult.value
            )
        }

    @Composable
    override fun compose() {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            textEntryFun(
                "Service",
                true,
                initialText = nameResult.value,
                singleLine = true,
                onTextChange = {
                    nameResult.value = it
                },
                onErrorChange = {
                    nameResult.error = it
                },
                modifier = Modifier.weight(1f),
            )
            dateEntryFun(
                "Start Date",
                true,
                initialTime = startDateResult.value?.time,
                singleLine = true,
                onValueChange = {
                    startDateResult.value = it
                },
                onErrorChange = {
                    startDateResult.error = it
                },
                modifier = Modifier.weight(1f),
            )
            dateEntryFun(
                "End Date",
                false,
                initialTime = startDateResult.value?.time,
                singleLine = true,
                onValueChange = {
                    endDateResult.value = it
                },
                onErrorChange = {
                    endDateResult.error = it
                },
                modifier = Modifier.weight(1f)
            )
            numEntryFun(
                "Quantity",
                true,
                initialVal = quantityResult.value,
                singleLine = true,
                onValueChange = {
                    quantityResult.value = it
                },
                onErrorChange = {
                    quantityResult.error = it
                },
                modifier = Modifier.weight(1f)
            )
            numEntryFun(
                "Price",
                true,
                initialVal = priceResult.value.toDouble(),
                singleLine = true,
                onValueChange = {
                    priceResult.value = it.toBigDecimal()
                },
                onErrorChange = {
                    priceResult.error = it
                },
                modifier = Modifier.weight(1f),
            )
            textEntryFun(
                "Description",
                true,
                initialText = descriptionResult.value,
                onTextChange = {
                    descriptionResult.value = it
                },
                onErrorChange = {
                    descriptionResult.error = it
                },
                singleLine = true,
                modifier = Modifier.weight(2f),
            )
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

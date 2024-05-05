package screens

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.textEntry
import layouts.InvoiceTrackingItem
import storage.DatabaseManager
import storage.InvoiceColumns

// TODO allow marking as paid
// TODO add recreating pdf
class InvoiceTrackingScreen : Screen {
    var iteration: Int = 0
    var _rows: SnapshotStateList<InvoiceTrackingItem> = mutableListOf<InvoiceTrackingItem>().toMutableStateList()
    var modifier: Modifier = Modifier

    fun loadRows(filter: String) {
        _rows.clear()

        (if (filter.isEmpty()) DatabaseManager.selectAllInvoices() else DatabaseManager.searchInvoices(
            InvoiceColumns.CLIENT_BUSINESS_NAME, // TODO add searching for other attributes
            filter
        )).map { invoice ->
            _rows.add(InvoiceTrackingItem(iteration++, invoice))
        }
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        loadRows("")
        val rows = remember { _rows }

        Column(modifier = modifier.padding(PaddingValues(10.dp))) {
            textEntry("Search by Client", false, onTextChange = {
                loadRows(it)
            })
            val state = rememberLazyListState()

            Box(modifier = Modifier.weight(5f)) {
                LazyColumn(state = state) {
                    items(
                        items = rows,
                        key = { row ->
                            row.id
                        }
                    ) { row ->
                        row.compose()
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(state)
                )
            }
            Button(
                onClick = {
                    navigator.pop()
                },
                modifier = Modifier.height(40.dp).fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }

}

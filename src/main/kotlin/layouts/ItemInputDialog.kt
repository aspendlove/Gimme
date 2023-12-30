package layouts

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.CustomButton
import compose.icons.FeatherIcons
import compose.icons.feathericons.XCircle
import screens.ItemLoadScreen
import storage.Item
import storage.StateBundle

class ItemInputDialog(startingItems: MutableList<Item>, private val onSave: () -> Unit, unfinishedRows: List<ItemInputRowDialog> = listOf()) {

    private var iteration: Int = 0

    private val _rows: MutableList<ItemInputRowDialog> = (unfinishedRows + startingItems.map { item ->
        ItemInputRowDialog(unfinishedRows.size + iteration++, onSave, item)
    }).toMutableList()

    init {
        if(_rows.isEmpty()) {
            _rows.add(ItemInputRowDialog(iteration++, onSave))
        }
    }

    var modifier: Modifier = Modifier

    val isError: Boolean
        get() {
            return _rows.fold(false) { running, row ->
                running || row.isError
            }
        }
    val results: List<Item>
        get() {
            return _rows.map { row ->
                row.result
            }
        }

    @Composable
    fun compose() {
        val navigator = LocalNavigator.currentOrThrow
        val rows = _rows.toMutableStateList()
        val newButton = CustomButton({
            val newRow = ItemInputRowDialog(iteration++, onSave)
            rows.add(newRow)
            _rows.add(newRow)
        }, "New Service")
        val loadButton = CustomButton({
            StateBundle.clear()
            _rows.map {row ->
                StateBundle.items.add(row.result)
            }
            navigator.replace(ItemLoadScreen())
        }, "Load Service")
        Column(modifier = modifier.padding(PaddingValues(10.dp))) {
            val state = rememberLazyListState()

            Box(modifier = Modifier.weight(5f)) {
                LazyColumn(state = state) {
                    items(
                        items = rows,
                        key = { row ->
                            row.id
                        }
                    ) { row ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    if (_rows.size <= 1) {
                                        val newRow = ItemInputRowDialog(iteration++, onSave)
                                        rows.add(newRow)
                                        _rows.add(newRow)
                                    }
                                    rows.remove(row)
                                    _rows.remove(row)
                                    StateBundle.items.remove(row.item)
                                },
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Icon(FeatherIcons.XCircle, "Delete")
                            }
                            row.compose()
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(state)
                )
            }
            Row {
                newButton.addModifier(Modifier.weight(1f))
                loadButton.addModifier(Modifier.weight(1f))
                newButton.compose()
                loadButton.compose()
//                Button(
//                    onClick = {
//                        val newRow = ItemInputRowDialog(iteration++, onSave)
//                        rows.add(newRow)
//                        _rows.add(newRow)
//                    },
//                    modifier = Modifier.height(40.dp).weight(1f)
//                ) {
//                    Text("New Service")
//                }
//                Button(
//                    onClick = {
//                        navigator.replace(ItemLoadScreen())
//                    },
//                    modifier = Modifier.height(40.dp).weight(1f)
//                ) {
//                    Text("Load Service")
//                }
            }
        }
    }
}

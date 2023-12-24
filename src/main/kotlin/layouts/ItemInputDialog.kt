package layouts

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.XCircle
import storage.Item

class ItemInputDialog {

    private var iteration: Int = 0

    private val _rows: MutableList<ItemInputRowDialog> = mutableListOf(ItemInputRowDialog(iteration++))

    var modifier: Modifier = Modifier

    val isError: Boolean
        get() {
//            var isErrorTentative = false
//            for (row in _rows) {
//                if (row.isError) {
//                    isErrorTentative = true
//                }
//            }
//            return isErrorTentative
            return _rows.fold(false) { running, row ->
                running || row.isError
            }
        }
    val results: List<Item>
        get() {
//            val returnList: MutableList<Item> = mutableListOf()
//
//            for (row in _rows) {
//                returnList.add(row.result)
//            }
//
//
//            return returnList.toList()
            return _rows.map { row ->
                row.result
            }
        }

    @Composable
    fun compose() {
        val rows = _rows.toMutableStateList()
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
                                    if (_rows.size < 2) {
                                        val newRow = ItemInputRowDialog(iteration++)
                                        rows.add(newRow)
                                        _rows.add(newRow)
                                    }
                                    rows.remove(row)
                                    _rows.remove(row)
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
            Button(
                onClick = {
                    val newRow = ItemInputRowDialog(iteration++)
                    rows.add(newRow)
                    _rows.add(newRow)
                },
                modifier = Modifier.height(40.dp).fillMaxWidth()
            ) {
                Text("Add Service")
            }
        }
    }
}

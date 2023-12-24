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
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import layouts.ItemInputRowDialog
import layouts.LoadScreenItem

abstract class LoadScreen<T>: Screen {
    var iteration: Int = 0
    private var _rows: MutableList<LoadScreenItem<T>> = mutableListOf()
    var modifier: Modifier = Modifier
    @Composable
    abstract fun loadRows(): MutableList<LoadScreenItem<T>>

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        _rows = loadRows()
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
                Text("Cancel")
            }
        }
    }
}

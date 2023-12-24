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
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.SearchBar
import layouts.ItemInputRowDialog
import layouts.LoadScreenItem

abstract class LoadScreen<T> : Screen {
    var iteration: Int = 0
    protected var _rows: SnapshotStateList<LoadScreenItem<T>> = mutableListOf<LoadScreenItem<T>>().toMutableStateList()
    var modifier: Modifier = Modifier


    abstract fun loadRows(navigator: Navigator, filter: String)

    abstract fun goToPreviousScreen(navigator: Navigator)

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        loadRows(navigator, "")
        val rows = remember { _rows }
        val searchBar = SearchBar("Search") { query ->
            loadRows(navigator, query)
        }
        Column(modifier = modifier.padding(PaddingValues(10.dp))) {
            searchBar.compose()
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
                    goToPreviousScreen(navigator)
                },
                modifier = Modifier.height(40.dp).fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}

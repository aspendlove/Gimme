package screens

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.CustomButton
import compose.icons.FeatherIcons
import compose.icons.feathericons.XCircle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import layouts.ItemInputRowDialog
import storage.Item
import storage.StateBundle
import javax.swing.JOptionPane

class ItemCreationScreen : Screen {
    private var _showSnackbar: MutableState<Boolean> = mutableStateOf(false)
    private val snackbarVisibleTime: Long = 3000

    private var iteration: Int = 0

    private val _rows: MutableList<ItemInputRowDialog> = mutableListOf()

    private val onSave: () -> Unit = {
        CoroutineScope(Dispatchers.Default).launch {
            _showSnackbar.value = true
            delay(snackbarVisibleTime)
            _showSnackbar.value = false
        }
    }

    private fun addItem(item: Item) {
        _rows.add(ItemInputRowDialog(iteration++, onSave, item))
    }

    init {
        if (_rows.isEmpty()) {
            _rows.add(ItemInputRowDialog(iteration++, onSave))
        }
    }

    val isError: Boolean
        get() {
            return _rows.fold(false) { running, row ->
                running || row.isError
            }
        }
    val result: List<Item>
        get() {
            return _rows.map { row ->
                row.result
            }
        }

    @Composable
    override fun Content() {
        val showSnackbar by remember { _showSnackbar }
        val navigator = LocalNavigator.currentOrThrow

        val backCustomButton = CustomButton({
            if (isError) {
                JOptionPane.showMessageDialog(
                    null,
                    "Please fill out all required fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
                return@CustomButton
            }
            StateBundle.items = result.toMutableList()
            navigator.pop()
        }, "Back")
        val forwardCustomButton = CustomButton({
            if (isError) {
                JOptionPane.showMessageDialog(
                    null,
                    "Please fill out all required fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
                return@CustomButton
            }
            StateBundle.items = result.toMutableList()
            navigator += NoteCreationScreen()
        }, "Forward")
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            val rows = _rows.toMutableStateList()
            val newButton = CustomButton({
                val newRow = ItemInputRowDialog(iteration++, onSave)
                rows.add(newRow)
                _rows.add(newRow)
            }, "New Service")
            val loadButton = CustomButton({
                navigator += ItemLoadScreen(::addItem)
            }, "Load Service")
            Column(modifier = Modifier.padding(PaddingValues(10.dp)).weight(3f)) {
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
                }
            }
            Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(0.dp, 10.dp, 0.dp, 0.dp)) {
                val commonModifier = Modifier.fillMaxWidth().weight(1f)
                backCustomButton.addModifier(commonModifier)
                forwardCustomButton.addModifier(commonModifier)
                backCustomButton.compose()
                forwardCustomButton.compose()
            }
        }
        if (showSnackbar) {
            Snackbar(
                modifier = Modifier
                    .zIndex(2f)
            ) {
                Text("Item saved successfully!") // Adjust message as needed
            }
        }
    }

}

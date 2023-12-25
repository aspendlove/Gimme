package screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.CustomButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import layouts.ItemInputDialog
import storage.Item
import storage.StateBundle
import javax.swing.JOptionPane

class ItemCreationScreen: Screen {
    private var _showSnackbar: MutableState<Boolean> = mutableStateOf(false)
    private val snackbarVisibleTime: Long = 3000

    private val itemInputDialog = ItemInputDialog(StateBundle.items) {
        CoroutineScope(Dispatchers.Default).launch {
            _showSnackbar.value = true
            delay(snackbarVisibleTime)
            _showSnackbar.value = false
        }
    }

    val isError: Boolean
        get() = itemInputDialog.isError
    val result: List<Item>
        get() = itemInputDialog.results
    @Composable
    override fun Content() {
        var showSnackbar by remember { _showSnackbar }
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
            navigator += NoteCreationScreen() // TODO add items to state bundle
        }, "Forward")
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            itemInputDialog.modifier = Modifier.weight(3f)
            itemInputDialog.compose()
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

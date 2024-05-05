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
import components.ValueErrorPair
import components.textEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import storage.DatabaseManager
import storage.Note
import storage.StateBundle

class NoteCreationScreen : Screen {

    private val notesResult: ValueErrorPair<String> = ValueErrorPair("", false)

    val result: String
        get() = notesResult.value

    @Composable
    override fun Content() {
        var showSnackbar by remember { mutableStateOf(false) }
        val snackbarVisibleTime: Long by remember { mutableStateOf(3000) } // Delay in milliseconds
        val navigator = LocalNavigator.currentOrThrow

        val loadCustomButton = CustomButton({
            navigator.replace(NoteLoadScreen())
        }, "Load Note")
        val saveCustomButton = CustomButton({
            DatabaseManager.insertNote(result)
            CoroutineScope(Dispatchers.Default).launch {
                showSnackbar = true
                delay(snackbarVisibleTime)
                showSnackbar = false
            }
        }, "Save Note")
        val backCustomButton = CustomButton({
            navigator.pop()
        }, "Back")
        val forwardCustomButton = CustomButton({
            StateBundle.notes = Note(result)
            navigator += SummaryScreen()
        }, "Forward")
        Column(modifier = Modifier.fillMaxHeight()) {
            textEntry(
                "Notes to be placed in the footer of the invoice",
                false,
                singleLine = false,
                onTextChange = {
                    notesResult.value = it
                },
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.75F),
                initialText = StateBundle.notes.note
            )
            Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(0.dp, 10.dp, 0.dp, 0.dp)) {
                loadCustomButton.addModifier(Modifier.weight(1f))
                saveCustomButton.addModifier(Modifier.weight(1f))
                loadCustomButton.compose()
                saveCustomButton.compose()
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
                Text("Note saved successfully!") // Adjust message as needed
            }
        }
    }
}

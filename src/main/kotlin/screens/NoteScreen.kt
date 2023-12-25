package screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.CustomButton
import components.NonRequiredTextMultiline
import storage.DatabaseManager
import storage.Note
import storage.StateBundle

class NoteScreen : Screen {
    private val notesEntry =
        NonRequiredTextMultiline("Notes to be placed in the footer of the invoice", StateBundle.notes.note)

    val result: String
        get() = notesEntry.result

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val loadCustomButton = CustomButton({
            navigator.replace(NoteLoadScreen())
        }, "Load Note")
        val saveCustomButton = CustomButton({
            DatabaseManager.insertNote(result)
        }, "Save Note")
        val backCustomButton = CustomButton({
            navigator.pop()
        }, "Back")
        val forwardCustomButton = CustomButton({
            StateBundle.notes = Note(result)
            navigator += SummaryScreen()
        }, "Forward")
        Column {
            notesEntry.addModifier(Modifier.fillMaxWidth().padding(100.dp).weight(7f))
            notesEntry.compose()
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
    }
}

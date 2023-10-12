package screens

import RequiredText
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import storage.User

class UserCreationDialog(private val onCloseDelegate: () -> Unit) {
    private val businessNameEntry = RequiredText("Business Name")
    private val contactNameEntry = RequiredText("Contact Name")
    private val subtitleEntry = RequiredText("Subtitle")
    private val streetEntry = RequiredText("Street")
    private val cityEntry = RequiredText("City")
    private val stateEntry = RequiredText("State")
    private val zipEntry = RequiredText("Zip Code")
    private val emailEntry = RequiredText("Email")
    private val phoneEntry = RequiredText("Phone")

    val isError: Boolean
        get() = businessNameEntry.isError ||
                contactNameEntry.isError ||
                subtitleEntry.isError ||
                streetEntry.isError ||
                cityEntry.isError ||
                stateEntry.isError ||
                zipEntry.isError ||
                emailEntry.isError ||
                phoneEntry.isError

    val result: User
        get() = User(
            businessNameEntry.result,
            contactNameEntry.result,
            subtitleEntry.result,
            streetEntry.result,
            cityEntry.result,
            stateEntry.result,
            zipEntry.result.toInt(),
            emailEntry.result,
            phoneEntry.result
        )

//    var _modifier: Modifier = Modifier

    @Composable
    @Preview
    fun compose() {
        Dialog(
            onCloseRequest = { onCloseDelegate() },
            state = rememberDialogState(
                position = WindowPosition(Alignment.Center),
                size = DpSize(500.dp, 600.dp)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(PaddingValues(20.dp))
            ) {
                businessNameEntry.compose()
                contactNameEntry.compose()
                subtitleEntry.compose()
                streetEntry.compose()
                cityEntry.compose()
                stateEntry.compose()
                zipEntry.compose()
                emailEntry.compose()
                phoneEntry.compose()
            }
        }
    }
}
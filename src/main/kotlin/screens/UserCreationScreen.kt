package screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.CustomButton
import components.RequiredText
import components.Title
import storage.User

class UserCreationScreen : Screen {
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

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val userTitle = Title("Create or Select a User")
        val presetButton = CustomButton({
            // TODO load dialog
        }, "Load User")
        val saveButton = CustomButton({
            // TODO save dialog
        }, "Save User")
        val backCustomButton = CustomButton({
            navigator.pop()
        }, "Back")
        val forwardCustomButton = CustomButton({
            // TODO Client page
        }, "Forward")
        Column(
            modifier = Modifier.fillMaxWidth().padding(PaddingValues(20.dp))
        ) {
            userTitle.compose()
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val commonModifier = Modifier.fillMaxWidth().weight(1f)
                presetButton.addModifier(commonModifier)
                saveButton.addModifier(commonModifier)
                presetButton.compose()
                saveButton.compose()
            }
            businessNameEntry.compose()
            contactNameEntry.compose()
            subtitleEntry.compose()
            streetEntry.compose()
            cityEntry.compose()
            stateEntry.compose()
            zipEntry.compose()
            emailEntry.compose()
            phoneEntry.compose()
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

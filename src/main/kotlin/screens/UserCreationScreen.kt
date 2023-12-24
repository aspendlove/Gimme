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
import storage.StateBundle
import storage.User
import javax.swing.JOptionPane

class UserCreationScreen : Screen {
    private val businessNameEntry = RequiredText("Business Name", StateBundle.user.businessName)
    private val contactNameEntry = RequiredText("Contact Name", StateBundle.user.contactName)
    private val subtitleEntry = RequiredText("Subtitle", StateBundle.user.subtitle)
    private val streetEntry = RequiredText("Street", StateBundle.user.street)
    private val cityEntry = RequiredText("City", StateBundle.user.city)
    private val stateEntry = RequiredText("State", StateBundle.user.state)
    private val zipEntry =
        RequiredText("Zip Code", if (StateBundle.user.zip != -1) StateBundle.user.zip.toString() else "")
    private val emailEntry = RequiredText("Email", StateBundle.user.email)
    private val phoneEntry = RequiredText("Phone", StateBundle.user.phone)

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
            navigator.replace(UserLoadScreen())
        }, "Load User")
        val saveButton = CustomButton({
            TODO("Save Dialog")
        }, "Save User")
        val backCustomButton = CustomButton({
            if (isError) {
                JOptionPane.showMessageDialog(
                    null,
                    "Please fill out all required fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
                return@CustomButton;
            }
            StateBundle.user = result
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
                return@CustomButton;
            }
            StateBundle.user = result
            navigator += ClientCreationScreen()
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

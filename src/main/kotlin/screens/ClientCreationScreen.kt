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
import components.RequiredText
import components.Title
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import storage.Client
import storage.DatabaseManager
import storage.StateBundle
import javax.swing.JOptionPane

class ClientCreationScreen : Screen {

    private val businessNameEntry = RequiredText("Business Name", StateBundle.client.businessName)
    private val contactNameEntry = RequiredText("Contact Name", StateBundle.client.contactName)
    private val streetEntry = RequiredText("Street", StateBundle.client.street)
    private val cityEntry = RequiredText("City", StateBundle.client.city)
    private val stateEntry = RequiredText("State", StateBundle.client.state)
    private val zipEntry =
        RequiredText("Zip Code", if (StateBundle.client.zip != -1) StateBundle.client.zip.toString() else "")
    private val emailEntry = RequiredText("Email", StateBundle.client.email)
    private val phoneEntry = RequiredText("Phone", StateBundle.client.phone)


    val isError: Boolean
        get() = businessNameEntry.isError ||
                contactNameEntry.isError ||
                streetEntry.isError ||
                cityEntry.isError ||
                stateEntry.isError ||
                zipEntry.isError ||
                emailEntry.isError ||
                phoneEntry.isError

    val result: Client
        get() = Client(
            businessNameEntry.result,
            contactNameEntry.result,
            streetEntry.result,
            cityEntry.result,
            stateEntry.result,
            zipEntry.result.toInt(),
            emailEntry.result,
            phoneEntry.result
        )

    @Composable
    override fun Content() {
        var showSnackbar by remember { mutableStateOf(false) }
        val snackbarVisibleTime: Long by remember { mutableStateOf(3000) } // Delay in milliseconds
        val navigator = LocalNavigator.currentOrThrow
        val userTitle = Title("Create or Select a Client")
        val presetButton = CustomButton({
            navigator.replace(ClientLoadScreen())
        }, "Load / Edit Clients")
        val saveButton = CustomButton({
            if (isError) {
                JOptionPane.showMessageDialog(
                    null,
                    "Please fill out all required fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
                return@CustomButton
            }
            DatabaseManager.insertClient(result)
            CoroutineScope(Dispatchers.Default).launch {
                showSnackbar = true
                delay(snackbarVisibleTime)
                showSnackbar = false
            }
        }, "Save Client")
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
            StateBundle.client = result
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
            StateBundle.client = result
            navigator += ItemCreationScreen()
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

        if (showSnackbar) {
            Snackbar(
                modifier = Modifier
                    .zIndex(2f)
            ) {
                Text("Client saved successfully!")
            }
        }
    }
}

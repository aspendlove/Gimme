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
import components.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import storage.Client
import storage.DatabaseManager
import storage.StateBundle
import javax.swing.JOptionPane

class ClientCreationScreen : Screen {
    private val businessNameResult = ValueErrorPair(StateBundle.client.businessName, true)
    private val contactNameResult = ValueErrorPair(StateBundle.client.contactName, true)
    private val streetResult = ValueErrorPair(StateBundle.client.street, true)
    private val cityResult = ValueErrorPair(StateBundle.client.city, true)
    private val stateResult = ValueErrorPair(StateBundle.client.state, true)
    private val zipResult = ValueErrorPair(StateBundle.client.zip, true)
    private val emailResult = ValueErrorPair(StateBundle.client.email, true)
    private val phoneResult = ValueErrorPair(StateBundle.client.phone, true)


    val isError: Boolean
        get() = businessNameResult.error ||
                contactNameResult.error ||
                streetResult.error ||
                cityResult.error ||
                stateResult.error ||
                zipResult.error ||
                emailResult.error ||
                phoneResult.error

    val result: Client
        get() = Client(
            businessNameResult.value,
            contactNameResult.value,
            streetResult.value,
            cityResult.value,
            stateResult.value,
            zipResult.value,
            emailResult.value,
            phoneResult.value
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

            textEntryFun(
                "Business Name",
                true,
                initialText = businessNameResult.value,
                singleLine = true,
                onTextChange = {
                    businessNameResult.value = it
                },
                onErrorChange = {
                    businessNameResult.error = it
                },
            )
            textEntryFun(
                "Contact Name",
                true,
                initialText = contactNameResult.value,
                singleLine = true,
                onTextChange = {
                    contactNameResult.value = it
                },
                onErrorChange = {
                    contactNameResult.error = it
                },
            )
            textEntryFun(
                "Street",
                true,
                initialText = streetResult.value,
                singleLine = true,
                onTextChange = {
                    streetResult.value = it
                },
                onErrorChange = {
                    streetResult.error = it
                },
            )
            textEntryFun(
                "City",
                true,
                initialText = cityResult.value,
                singleLine = true,
                onTextChange = {
                    cityResult.value = it
                },
                onErrorChange = {
                    cityResult.error = it
                },
            )
            textEntryFun(
                "State",
                true,
                initialText = stateResult.value,
                singleLine = true,
                onTextChange = {
                    stateResult.value = it
                },
                onErrorChange = {
                    stateResult.error = it
                },
            )
            zipEntryFun(
                "Zip Code",
                true,
                initialVal = zipResult.value,
                onValueChange = {
                    zipResult.value = it
                },
                onErrorChange = {
                    zipResult.error = it
                },
            )
            textEntryFun(
                "Email",
                true,
                initialText = emailResult.value,
                singleLine = true,
                onTextChange = {
                    emailResult.value = it
                },
                onErrorChange = {
                    emailResult.error = it
                },
            )
            textEntryFun(
                "Phone",
                true,
                initialText = phoneResult.value,
                singleLine = true,
                onTextChange = {
                    phoneResult.value = it
                },
                onErrorChange = {
                    phoneResult.error = it
                },
            )
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

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
import storage.DatabaseManager
import storage.StateBundle
import storage.User
import javax.swing.JOptionPane

// TODO add optional addresses and alternative payment methods
class UserCreationScreen : Screen {

    private val businessNameResult = ValueErrorPair(StateBundle.user.businessName, true)
    private val contactNameResult = ValueErrorPair(StateBundle.user.contactName, true)
    private val subtitleResult = ValueErrorPair(StateBundle.user.subtitle, true)
    private val streetResult = ValueErrorPair(StateBundle.user.street, true)
    private val cityResult = ValueErrorPair(StateBundle.user.city, true)
    private val stateResult = ValueErrorPair(StateBundle.user.state, true)
    private val zipResult = ValueErrorPair(StateBundle.user.zip, true)
    private val emailResult = ValueErrorPair(StateBundle.user.email, true)
    private val phoneResult = ValueErrorPair(StateBundle.user.phone, true)

    val isError: Boolean
        get() = businessNameResult.error ||
                contactNameResult.error ||
                subtitleResult.error ||
                streetResult.error ||
                cityResult.error ||
                stateResult.error ||
                zipResult.error ||
                emailResult.error ||
                phoneResult.error

    val result: User
        get() = User(
            businessNameResult.value,
            contactNameResult.value,
            subtitleResult.value,
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
        val userTitle = Title("Create or Select a User")
        val presetButton = CustomButton({
            navigator.replace(UserLoadScreen())
        }, "Load / Edit Users")
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
            DatabaseManager.insertUser(result)
            CoroutineScope(Dispatchers.Default).launch {
                showSnackbar = true
                delay(snackbarVisibleTime)
                showSnackbar = false
            }
        }, "Save User")
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
                return@CustomButton
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
            textEntry(
                "Business Name",
                true,
                initialText = businessNameResult.value,
                onTextChange = {
                    businessNameResult.value = it
                },
                onErrorChange = {
                    businessNameResult.error = it
                },
            )
            textEntry(
                "Contact Name",
                true,
                initialText = contactNameResult.value,
                onTextChange = {
                    contactNameResult.value = it
                },
                onErrorChange = {
                    contactNameResult.error = it
                },
            )
            textEntry(
                "Subtitle",
                true,
                initialText = subtitleResult.value,
                onTextChange = {
                    subtitleResult.value = it
                },
                onErrorChange = {
                    subtitleResult.error = it
                },
            )
            textEntry(
                "Street",
                true,
                initialText = streetResult.value,
                onTextChange = {
                    streetResult.value = it
                },
                onErrorChange = {
                    streetResult.error = it
                },
            )
            textEntry(
                "City",
                true,
                initialText = cityResult.value,
                onTextChange = {
                    cityResult.value = it
                },
                onErrorChange = {
                    cityResult.error = it
                },
            )
            textEntry(
                "State",
                true,
                initialText = stateResult.value,
                onTextChange = {
                    stateResult.value = it
                },
                onErrorChange = {
                    stateResult.error = it
                },
            )
            zipEntry(
                "Zip Code",
                true,
                initialVal = if (zipResult.value != -1) {
                    zipResult.value
                } else {
                    null
                },
                onValueChange = {
                    zipResult.value = it
                },
                onErrorChange = {
                    zipResult.error = it
                }
            )
            textEntry(
                "Email",
                true,
                initialText = emailResult.value,
                onTextChange = {
                    emailResult.value = it
                },
                onErrorChange = {
                    emailResult.error = it
                },
            )
            textEntry(
                "Phone",
                true,
                initialText = phoneResult.value,
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
                Text("User saved successfully!")
            }
        }
    }
}

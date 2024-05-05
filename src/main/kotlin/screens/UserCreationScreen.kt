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

class UserCreationScreen : Screen {

    private val businessNameResult = ValueErrorPair("", false)
    private val contactNameResult = ValueErrorPair("", false)
    private val subtitleResult = ValueErrorPair("", false)
    private val streetResult = ValueErrorPair("", false)
    private val cityResult = ValueErrorPair("", false)
    private val stateResult = ValueErrorPair("", false)
    private val zipResult = ValueErrorPair(0, false)
    private val emailResult = ValueErrorPair("", false)
    private val phoneResult = ValueErrorPair("", false)

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
            textEntryFun(
                "Business Name",
                true,
                initialText = StateBundle.user.businessName,
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
                initialText = StateBundle.user.contactName,
                onTextChange = {
                    contactNameResult.value = it
                },
                onErrorChange = {
                    contactNameResult.error = it
                },
            )
            textEntryFun(
                "Subtitle",
                true,
                initialText = StateBundle.user.subtitle,
                onTextChange = {
                    subtitleResult.value = it
                },
                onErrorChange = {
                    subtitleResult.error = it
                },
            )
            textEntryFun(
                "Street",
                true,
                initialText = StateBundle.user.street,
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
                initialText = StateBundle.user.city,
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
                initialText = StateBundle.user.state,
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
                initialVal = if (StateBundle.user.zip != -1) {
                    StateBundle.user.zip
                } else {
                    0
                },
                onValueChange = {
                    zipResult.value = it
                },
                onErrorChange = {
                    zipResult.error = it
                }
            )
            textEntryFun(
                "Email",
                true,
                initialText = StateBundle.user.email,
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
                initialText = StateBundle.user.phone,
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

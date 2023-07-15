package screens

import NonRequiredText
import RequiredText
import RequiredTextMultiline
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class UserSelectionForm : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Column(modifier = Modifier.fillMaxSize()) {
            Text("storage.User")
            Column{
                Text("Scrollable goes here")
                Button(onClick={
                    navigator.push(UserCreationForm())
                }, modifier = Modifier.fillMaxWidth()){
                    Text("Create New storage.User")
                }
                Button(onClick={
                    // TODO save user selection here
                    navigator.push(UserSelectionForm())
                }, modifier = Modifier.fillMaxWidth()){
                    Text("Continue")
                }
            }
        }
    }
}

class UserCreationForm : Screen {
    private val businessEntry: RequiredText = RequiredText("Business Name")
    private val nameEntry: RequiredText = RequiredText("Owner Name")
    private val descriptionEntry: RequiredTextMultiline = RequiredTextMultiline("Job Description and Certifications")
    private val streetEntry: RequiredText = RequiredText("Street")
    private val cityEntry: RequiredText = RequiredText("City")
    private val stateEntry: RequiredText = RequiredText("State")
    private val zipEntry: RequiredText = RequiredText("Zip")
    private val emailEntry: NonRequiredText = NonRequiredText("Email")
    private val phoneEntry: NonRequiredText = NonRequiredText("Phone")

    @Composable
    override fun Content() {
        Column(modifier = Modifier.fillMaxSize().padding(PaddingValues(10.dp))) {
            Text("storage.User")
            Divider()
            businessEntry.compose()
            nameEntry.compose()
            descriptionEntry.compose()
            streetEntry.compose()
            cityEntry.compose()
            stateEntry.compose()
            zipEntry.compose()
            emailEntry.compose()
            phoneEntry.compose()
        }
    }
}
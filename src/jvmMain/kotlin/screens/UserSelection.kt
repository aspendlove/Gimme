package screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class UserSelectionForm : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Column(modifier = Modifier.fillMaxSize()) {
            Text("User")
            Column{
                Text("Scrollable goes here")
                Button(onClick={

                }, modifier = Modifier.fillMaxWidth()){
                    Text("Create New User")
                }
                Button(onClick={
                    navigator.push(UserSelectionForm())
                }, modifier = Modifier.fillMaxWidth()){
                    Text("Continue")
                }
            }
        }
    }
}

class UserCreationForm : Screen {
    override fun Content() {
        TODO("Not yet implemented")
    }
}
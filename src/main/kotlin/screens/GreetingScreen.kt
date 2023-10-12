package screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.Button

class GreetingScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val continueButton = Button({
            navigator += UserCreationDialog()
        }, "Continue")
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Welcome to Gimme!\nReady to make an invoice?")
            continueButton.compose()
        }
    }
}
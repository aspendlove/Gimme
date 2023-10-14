package screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.CustomButton
import components.Title

class GreetingScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val trackCustomButton = CustomButton({
            // TODO track invoice screen
        }, "Track Invoices")
        val continueCustomButton = CustomButton({
            navigator += UserCreationScreen()
        }, "Create an Invoice")
        val greeting = Title(
            "Welcome to Gimme!\nReady to make an invoice?"
        )
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            greeting.compose()
            Row {
                trackCustomButton.compose()
                continueCustomButton.compose()
            }
            Text("© Aidan Spendlove 2023", color = Color.White, modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp))
        }
    }
}
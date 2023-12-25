package screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.Body
import components.CustomButton
import components.Title
import storage.StateBundle

class ConclusionScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val message = Title("Invoice saved and automatically opened")
        val resetButton = CustomButton({
            StateBundle.clear()
            navigator.replaceAll(GreetingScreen())
        }, "Back to home")
        Column{
            message.addModifier(Modifier.fillMaxWidth())
            resetButton.addModifier(Modifier.fillMaxWidth())
            message.compose()
            resetButton.compose()
        }
    }
}

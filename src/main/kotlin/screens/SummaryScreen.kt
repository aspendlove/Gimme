package screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.Body
import storage.StateBundle

class SummaryScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val userText= Body("hello")
        val clientText = Body("hello")
        val serviceText = Body("hello")
        val notesText = Body("hello")

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val cardModifier = Modifier.fillMaxWidth().weight(1f).padding(10.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(modifier = cardModifier) {
                    userText.compose()
                }
                Card(modifier = cardModifier) {
                    clientText.compose()
                }
            }
            Card(modifier = cardModifier) {
                serviceText.compose()
            }
            Card(modifier = cardModifier) {
                notesText.compose()
            }
        }
    }
}

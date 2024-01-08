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
import components.*

class GreetingScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val trackCustomButton = CustomButton({
            navigator += InvoiceTrackingScreen()
        }, "Track Invoices")
        val continueCustomButton = CustomButton({
            navigator += UserCreationScreen()
        }, "Create an Invoice")
        val greeting = Title(
            "Welcome to Gimme!\nReady to make an invoice?"
        )
        val testEntryRequired = TextEntry("required text entry", true, "required")
//        val testEntry = TextEntry("text entry", false)
//        val testNumEntryRequired = NumberEntryOld("num entry required", 0.1)
//        val testNumEntry = NumberEntryOld("num entry")
        val testNumEntry = NumberEntry("num entry", true,0.1)
        val testOldNumEntry = NumberEntryOld("num entry old", 0.5)
        val testEntrySub = TestEntry(title = "ding", required = true, singleLine = false)

        val testDateEntry = DateEntry("test date entry", required = true, initialValue = null)
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            greeting.compose()
            Row {
                trackCustomButton.compose()
                continueCustomButton.compose()
            }
            testDateEntry.compose()
            testEntrySub.compose()
//            testEntryRequired.compose()
            testNumEntry.compose()
//            testNumEntry.value = 0.87
//            testOldNumEntry.compose()
//            testEntrySub.compose()
//            CoroutineScope(Dispatchers.Default).launch {
//                delay(3000)
////                println(testNumEntry.value)
//                println(testNumEntry.value)
//            }
            Text("© Aidan Spendlove 2023", color = Color.White, modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp))
        }
    }
}

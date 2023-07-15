import screens.GreetingPage
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.FadeTransition

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun App() {
    Navigator(GreetingPage()) { navigator ->
        FadeTransition(navigator)
        //fade scale screen slide
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

data class DetailScreen(private val itemId: Int) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Button(onClick = {
            navigator.pop()
        }) {
            Text("New page!")
        }
    }
}

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        class HomeScreenModel : ScreenModel {
            var counter = 0
        }

        val screenModel = rememberScreenModel { HomeScreenModel() }
        val navigator = LocalNavigator.currentOrThrow

        Button(onClick = {
            screenModel.counter++
            navigator.push(DetailScreen(itemId = 123))
        }) {
            Text("View details and also ${screenModel.counter}")
        }
    }
}

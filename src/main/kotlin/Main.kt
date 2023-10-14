
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import screens.GreetingScreen

// TODO add notes to database and UI

fun main() = application {
    val windowState = rememberWindowState(size = DpSize(1000.dp, 800.dp))
    Window(onCloseRequest = ::exitApplication, state = windowState, title = "Gimme! Invoice Creator") {
        App();
    }
}

@Composable
fun App() {
    MaterialTheme(colors = darkColors()) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            Navigator(
                screen = GreetingScreen()
            )
        }
    }
}

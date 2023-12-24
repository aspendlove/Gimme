
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
import screens.ItemCreationScreen
import screens.LoadScreen
import storage.Client
import storage.DatabaseManager
import storage.User

fun main() = application {
//    DatabaseManager.insertUser(User(
//        "BusinessName",
//        "contactName",
//        "sub",
//        "street",
//        "city",
//        "state",
//        1,
//        "email",
//        "phone"
//    ))
//    DatabaseManager.insertClient(Client("Amy",
//        "Amy",
//        "1366 S Stewart St.",
//        "Salt Lake City",
//        "Utah",
//        84104,
//        "mrsamyj1@gmail.com",
//        "801-560-1810")
//    )
    val windowState = rememberWindowState(size = DpSize(1200.dp, 800.dp))
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


import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator

@Composable
fun App() {
    Navigator(
        screen = BasicNavigationScreen(index = 0),
        onBackPressed = {
            true
        }
    )

//    var isDialogOpen by remember { mutableStateOf(false) }
//
//    val userCreationDialog = UserCreationDialog {
//        isDialogOpen = false
//    }
//
//    Button(onClick = { isDialogOpen = true }) {
//        Text(text = "Open dialog")
//    }
//
//    if (isDialogOpen) {
//        userCreationDialog.compose()
//    }
}

fun main() = application {
    val windowState = rememberWindowState(size = DpSize(700.dp, 600.dp))
    Window(onCloseRequest = ::exitApplication, state=windowState) {
        App();
    }
}

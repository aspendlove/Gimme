package screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.CustomButton
import layouts.ItemInputDialog
import storage.StateBundle

class ItemCreationScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val itemInputDialog = ItemInputDialog()
        val backCustomButton = CustomButton({
            navigator.pop()
        }, "Back")
        val forwardCustomButton = CustomButton({
            navigator += NoteScreen() // TODO add items to state bundle
        }, "Forward")
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            itemInputDialog.modifier = Modifier.weight(3f)
            itemInputDialog.compose()
            Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(0.dp, 10.dp, 0.dp, 0.dp)) {
                val commonModifier = Modifier.fillMaxWidth().weight(1f)
                backCustomButton.addModifier(commonModifier)
                forwardCustomButton.addModifier(commonModifier)
                backCustomButton.compose()
                forwardCustomButton.compose()
            }
        }
    }

}

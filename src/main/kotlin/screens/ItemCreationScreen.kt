package screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import components.NonRequiredTextMultiline
import layouts.ItemInputDialog

class ItemCreationScreen: Screen {
    @Composable
    override fun Content() {
        val itemInputDialog = ItemInputDialog()
        val notes = NonRequiredTextMultiline("Footer Notes")
        notes.addModifier(Modifier.fillMaxWidth())
        Column {
            itemInputDialog.modifier = Modifier.weight(3f)
            itemInputDialog.compose()
            notes.compose()
        }
    }

}
package components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class CustomButton(private val onClick: () -> Unit, private val text: String) :
    CustomComponentBase(Modifier.height(40.dp).padding(3.dp)) {
    @Composable
    override fun compose() {
        Button(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text);
        }
    }
}
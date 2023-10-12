package components

import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class Button(private val onClick: () -> Unit, private val text: String) {
    private var _modifier: Modifier = Modifier.height(40.dp)
    var modifier: Modifier
        get() {
            return _modifier
        }
        set(value){
            _modifier = value;
        }
    @Composable
    fun compose() {
        Button(
            onClick = onClick,
            modifier = _modifier
        ) {
            Text(text);
        }
    }
}
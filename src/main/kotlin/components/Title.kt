package components

import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class Title(private val text: String) {
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
        Text(text,modifier)
    }
}
package components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class DefaultButton {
    private var _modifier: Modifier = Modifier.fillMaxWidth().height(40.dp)
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
            onClick = {

            },
            modifier = _modifier
        ) {

        }
    }
}
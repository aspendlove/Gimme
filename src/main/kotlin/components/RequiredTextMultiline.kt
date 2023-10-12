
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.TextFieldValue

class RequiredTextMultiline(private var title: String) {
    private var _text = ""
    private var _error = true

    val isError: Boolean
        get() = _error

    val result: String
        get() = _text

    var modifier:Modifier = Modifier

    @Composable
    @Preview
    fun compose() {
        var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }

        var error by remember { mutableStateOf(false) }
        var first by remember { mutableStateOf(true) }

        TextField(
            value = text,
            onValueChange = {
                text = it
                _text = text.text
                error = it.text.isEmpty()
                _error = error
            },
            label = { Text(title) },
            isError = error,
            trailingIcon = {
                Icon(Icons.Default.Star, "Star")
            },
            modifier = modifier.fillMaxWidth().onFocusChanged {
                if(it.isFocused) {
                    error = false
                    _error = false
                    return@onFocusChanged
                }
                if(!it.isFocused && first) {
                    first = false
                    return@onFocusChanged
                }
                error = _text.isEmpty()
                _error = error
            }
        )
    }
}


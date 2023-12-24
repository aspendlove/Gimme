package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

class RequiredText(private var title: String, private var initialText: String = "") : CustomComponentBase(Modifier.fillMaxWidth()) {
    private var _text = initialText
    private var _error = true

    init {
        if(initialText.isNotEmpty()) {
            _error = false
        }
    }


    val isError: Boolean
        get() = _error

    val result: String
        get() = _text

    @Composable
    @Preview
    override fun compose() {
        var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(initialText))
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
            singleLine = true,
            isError = error,
            trailingIcon = {
                Icon(Icons.Default.Star, "Star")
            },
            modifier = modifier.fillMaxWidth().onFocusChanged {
                if (it.isFocused) {
                    error = false
                    _error = false
                    return@onFocusChanged
                }
                if (!it.isFocused && first) {
                    first = false
                    return@onFocusChanged
                }
                error = _text.isEmpty()
                _error = error
            },
            textStyle = TextStyle(color = darkColors().onBackground)
        )
    }
}


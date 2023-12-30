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

class NumberEntry(private val title: String, private val required: Boolean, private val initialValue: Double? = null) :
    CustomComponentBase(Modifier.fillMaxWidth()) {
    private var _text = initialValue?.toString() ?: ""
    private var _error = required

    init {
        if (_text.isNotEmpty()) {
            _error = false
        }
    }

    val isError: Boolean
        get() = _error

    var value: Double
        get() = _text.toDouble()
        set(new) {
            _text = new.toString()
        }

    @Composable
    @Preview
    override fun compose() {
        var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(_text))
        }

        var error by remember { mutableStateOf(_error) }
        var first by remember { mutableStateOf(true) }

        TextField(
            value = text,
            onValueChange = { newTextFieldValue ->
                var newText = newTextFieldValue.text
                if (newText.startsWith('.')) {
                    newText = "0$newText"
                }
                val isValid = newText.fold(true) { running, currentChar ->
                    running && (currentChar.isDigit() || currentChar == '.')
                }

                if (!isValid) return@TextField

                text = TextFieldValue(newText)
                _text = newText

                if (required) {
                    error = newTextFieldValue.text.isEmpty()
                    _error = error
                }
            },
            label = { Text(title) },
            singleLine = true,
            isError = error,
            trailingIcon = {
                Icon(Icons.Default.Star, "Star")
            },
            modifier = modifier.fillMaxWidth().onFocusChanged {
                if (!required) return@onFocusChanged
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
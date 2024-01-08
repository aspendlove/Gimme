package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

abstract class Entry<T>(
    private val title: String,
    private val required: Boolean,
    private val singleLine: Boolean,
    initialText: String = "",
    modifier: Modifier = Modifier.fillMaxWidth()
) : ComponentBase(modifier) {

    private var _textField: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(initialText))
    private var _error: MutableState<Boolean> = mutableStateOf(required)
    private var _first: MutableState<Boolean> = mutableStateOf(true)

    var value: T
        get() {
            val change = handleValueChanges(_textField.value.text)
            _textField.value = TextFieldValue(change.toString())
            return change
        }
        set(new) {
            _textField.value = TextFieldValue(new.toString())
        }
    val isError: Boolean
        get() = _error.value

    init {
        if (_textField.value.text.isNotEmpty()) {
            _error.value = false
        }
    }

    abstract fun handleValueChanges(textValue: String): T

    open fun onValueChange(new: TextFieldValue, old: TextFieldValue): TextFieldValue {
        return new
    }

    open fun checkErrorStatus(new: FocusState, first: Boolean): Boolean {
        if (!required) return false
        if (new.isFocused) {
            return false
        }
        return _textField.value.text.isEmpty() && !first
    }

    open fun onFocusChange(new: FocusState): TextFieldValue? {
        return null
    }

    @Composable
    @Preview
    final override fun compose() {
        var textField by remember {
            _textField
        }
        var error by remember {
            _error
        }
        var first by remember {
            _first
        }
        TextField(
            value = textField,
            onValueChange = { new: TextFieldValue ->
                val newValue = onValueChange(new, textField)
                textField = newValue
                _textField.value = newValue
                println(value)
            },
            label = { Text(title) },
            singleLine = singleLine,
            isError = error,
            trailingIcon = {
                if (required) {
                    Icon(Icons.Outlined.Star, "Required")
                }
            },
            modifier = modifier.then(Modifier.onFocusChanged { new: FocusState ->
                val newErrorStatus = checkErrorStatus(new, first)
                error = newErrorStatus
                _error.value = newErrorStatus
                if (!new.isFocused && first) {
                    first = false
                    _first.value = false
                }
                val modifiedText = onFocusChange(new)
                if (modifiedText != null) {
                    textField = modifiedText
                    _textField.value = modifiedText
                }
            }),
            textStyle = TextStyle(color = darkColors().onBackground)
        )
    }
}

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
    private var _visualError: MutableState<Boolean> = mutableStateOf(false)

    var value: T?
        get() {
            val change = handleValueChanges(_textField.value.text)
            _textField.value = TextFieldValue(change.second)
            return change.first
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

    abstract fun handleValueChanges(textValue: String): Pair<T?, String>

    open fun isEntryEmpty(text: String): Boolean {
        return text.isEmpty()
    }

    open fun onValueChange(new: TextFieldValue, old: TextFieldValue): TextFieldValue {
        return new
    }

    open fun checkValueErrorStatus(new: TextFieldValue, old: TextFieldValue) : Boolean {
        return false
    }

    open fun checkFocusErrorStatus(new: FocusState, first: Boolean, text: String): Boolean {
        return false
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
        var visualError by remember {
            _visualError
        }

        fun changeErrorStatus(errorStatus: Boolean) {
            error = errorStatus
            _error.value = errorStatus
        }

        TextField(
            value = textField,
            onValueChange = { new: TextFieldValue ->
                val newValue = onValueChange(new, textField)
                textField = newValue
                _textField.value = newValue
                changeErrorStatus(checkValueErrorStatus(new, textField))
                val visualStatus = if(!required && isEntryEmpty(textField.text)) {
                    false
                } else {
                    error || isEntryEmpty(textField.text)
                }
                visualError = visualStatus
                _visualError.value = visualStatus
                println(value)
            },
            label = { Text(title) },
            singleLine = singleLine,
            isError = visualError,
//            if(!required && isEntryEmpty(textField.text)) {
//                false
//            } else {
//                error
//            },
            trailingIcon = {
                if (required) {
                    Icon(Icons.Outlined.Star, "Required")
                }
            },
            modifier = modifier.then(Modifier.onFocusChanged { new: FocusState ->
                changeErrorStatus(checkFocusErrorStatus(new, first, textField.text))
                val visualStatus = if((!required && isEntryEmpty(textField.text)) || new.isFocused) {
                    false
                } else {
                    error && !first
                }
                visualError = visualStatus
                _visualError.value = visualStatus
                if (new.isFocused) {
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

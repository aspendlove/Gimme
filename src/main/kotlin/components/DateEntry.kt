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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import java.sql.Date
import java.text.ParseException
import java.text.SimpleDateFormat

class DateFormatException(message: String) : Exception(message)

class DateEntry(val title: String, private val required: Boolean = true, millisSinceEpoch: Long? = null) :
    CustomComponentBase(Modifier) {
    private var _text: String
    private var _error: Boolean

    init {
        if (millisSinceEpoch != null) {
            _text = SimpleDateFormat("MM/dd/yyyy").format(java.util.Date(millisSinceEpoch))
            _error = false
        } else {
            _text = "MM/DD/YYYY"
            _error = required
        }
    }


    val isError: Boolean
        get() = _error || _text.filter { toFilter ->
            toFilter.isDigit()
        }.length != 8


    val result: Date?
        get() {
            return if (isError) {
                null
            } else {
                try {
                    Date(
                        SimpleDateFormat("MM/dd/yyyy").parse(_text).time
                    )
                } catch(e: ParseException) {
                    throw DateFormatException("Malformed date in input field")
                }
            }
        }

    @Composable
    @Preview
    override fun compose() {
        val icon: @Composable (() -> Unit)? = if (required) {
            @Composable { Icon(Icons.Default.Star, "Star") }
        } else {
            null
        }

        var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(_text))
        }

        var error by remember { mutableStateOf(false) }
        var first by remember { mutableStateOf(true) }
        var cursorPlacement by remember { mutableStateOf(0) }

        TextField(value = text,
            textStyle = TextStyle(color = darkColors().onBackground),
            onValueChange = { newValue ->

                var digits = newValue.text.filter { toFilter -> toFilter.isDigit() }

                cursorPlacement = if (digits.length > 4) {
                    digits.length + 2
                } else if (digits.length > 2) {
                    digits.length + 1
                } else {
                    digits.length
                }

                if (digits.isEmpty()) {
                    text = TextFieldValue(
                        "MM/DD/YYYY", TextRange(cursorPlacement)
                    )
                } else {
                    while (digits.length < 8) {
                        digits += ' '
                    }

                    text = TextFieldValue(
                        digits.substring(0, 2) + "/" + digits.substring(2, 4) + "/" + digits.substring(4, 8),
                        TextRange(cursorPlacement)
                    )
                }


                _text = text.text
            },
            label = { Text(title) },
            singleLine = true,
            isError = error,
            trailingIcon = icon,
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
                error = _text.isEmpty() || _text.filter { toFilter ->
                    toFilter.isDigit()
                }.length != 8

                _error = error
            })
    }
}

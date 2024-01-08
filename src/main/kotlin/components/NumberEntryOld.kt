package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

class NumberEntryOld(private val title: String, initialValue: Double? = null) :
    ComponentBase(Modifier.fillMaxWidth()) {

    private var _textField: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(initialValue?.toString() ?: "0.0"))

    var value: Double
        get() {
            with(_textField) {
                if (value.text.endsWith('.')) {
                    value = TextFieldValue(value.text + '0')
                }
                return value.text.toDouble()
            }
        }
        set(new) {
            with(_textField) {
                value = TextFieldValue(new.toString())
            }
        }

    @Composable
    @Preview
    override fun compose() {
        var textField by remember {
            _textField
        }

        TextField(
            value = textField,
            onValueChange = { new: TextFieldValue ->
                var newText = new.text
                var newSelection = new.selection
                if (newText.startsWith('.')) {
                    newText = "0$newText"
                }
                if(newText.isEmpty()) {
                    newText = "0"
                    newSelection = TextRange(newSelection.max + 1)
                }
                if(newText.startsWith("0") && newText.length > 1 && newText[1] != '.') {
                    newText = newText.removePrefix("0")
                    println(newText)
                }
                var singlePoint = true
                val isValid = newText.fold(true) { running, currentChar ->
                    val valid = running && (currentChar.isDigit() || (currentChar == '.' && singlePoint))
                    if(currentChar == '.') {
                        singlePoint = false
                    }
                    return@fold valid
                }
                if (!isValid) return@TextField

                textField = TextFieldValue(newText, newSelection, new.composition)
            },
            label = { Text(title) },
            singleLine = true,
            modifier = modifier.fillMaxWidth(),
            textStyle = TextStyle(color = darkColors().onBackground)
        )
    }
}

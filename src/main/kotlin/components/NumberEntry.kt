package components

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class NumberEntry(title: String, initialValue: Double? = null) : Entry<Double>(
    title,
    required = false,
    singleLine = true,
    initialValue?.toString() ?: "0.0"
) {
    override fun handleValueChanges(textValue: String): Double {
        var text = textValue
        if(text.endsWith('.')) {
            text += '0'
        }
        return text.toDouble()
    }
    override fun onValueChange(new: TextFieldValue, old: TextFieldValue): TextFieldValue {
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
        }
        var singlePoint = true
        val isValid = newText.fold(true) { running, currentChar ->
            val valid = running && (currentChar.isDigit() || (currentChar == '.' && singlePoint))
            if(currentChar == '.') {
                singlePoint = false
            }
            return@fold valid
        }
        if (!isValid) return old

        return TextFieldValue(newText, newSelection, new.composition)
    }
}

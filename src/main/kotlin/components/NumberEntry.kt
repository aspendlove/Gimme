package components

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class NumberEntry(title: String, required: Boolean, initialValue: Double? = null) : Entry<Double>(
    title,
    required = required,
    singleLine = true,
    initialValue?.toString() ?: "0.0"
) {
    override fun handleValueChanges(textValue: String): Pair<Double?, String> {
        var text = textValue
        if(text.endsWith('.') || text == "-") {
            text += '0'
        }
        if(text.isNotEmpty()) {
            val numText = text.toDouble()
            return Pair(numText, numText.toString())
        } else {
            return Pair(null, text)
        }
    }

    override fun onValueChange(new: TextFieldValue, old: TextFieldValue): TextFieldValue {
        var newText = new.text
        var newSelection = new.selection
        if(newText.startsWith("-.")) {
            newText = "-0.${newText.removePrefix("-.")}"
            newSelection = TextRange(newSelection.end + 1)
        }
        if (newText.startsWith('.')) {
            newText = "0$newText"
            newSelection = TextRange(newSelection.end + 1)
        }
//        if(newText.isEmpty()) {
//            newText = "0"
//            newSelection = TextRange(newSelection.max + 1)
//        }
        if(newText.startsWith("0") && newText.length > 1 && newText[1] != '.') {
            newText = newText.removePrefix("0")
        }
        var singlePoint = true
        var singleNegativeSign = true
        var isValid = newText.fold(true) { running, currentChar ->
            val valid = running && (currentChar.isDigit() || (currentChar == '.' && singlePoint) || (currentChar == '-' && singleNegativeSign))
            if(currentChar == '.') {
                singlePoint = false
            }
            if(currentChar == '-') {
                singleNegativeSign = false
            }
            return@fold valid
        }
        val validNegativePlacement = if(!singleNegativeSign) {
            newText.startsWith('-')
        } else {
            true
        }
        isValid = isValid && validNegativePlacement

        if (!isValid) return old

        return TextFieldValue(newText, newSelection, new.composition)
    }
}

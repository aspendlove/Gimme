package components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import java.sql.Date
import java.text.ParseException
import java.text.SimpleDateFormat

data class ValueErrorPair<T>(var value: T, var error: Boolean)

private fun calculateErrorChange(
    calculatedError: Boolean,
    originalError: Boolean,
    onErrorChange: (Boolean) -> Unit
): Boolean {
    if (calculatedError != originalError) {
        onErrorChange(calculatedError)
    }
    return calculatedError
}

@Composable
fun textEntryFun(
    title: String,
    required: Boolean,
    onErrorChange: (Boolean) -> Unit = {},
    onTextChange: (String) -> Unit = {},
    initialText: String = "",
    singleLine: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {

    var error by remember { mutableStateOf(initialText.isEmpty() && required) }
    onErrorChange(error)
    var text by remember {
        mutableStateOf(TextFieldValue(initialText))
    }
    var first by remember { mutableStateOf(true) }
    TextField(
        value = text,
        onValueChange = {
            if (text.text != it.text) {
                onTextChange(it.text)
            }
            text = it
            if (required) {
                error = calculateErrorChange(it.text.isEmpty(), error, onErrorChange)
            }
        },
        label = { Text(title) },
        singleLine = singleLine,
        isError = error && !first,
        trailingIcon = {
            if (required) {
                Icon(Icons.Default.Star, "Star")
            }
        },
        modifier = modifier.fillMaxWidth().onFocusChanged {
            text = TextFieldValue(text.text, TextRange(text.text.length), text.composition)
            if (!required) return@onFocusChanged
            if (it.isFocused) {
                if(first) first = false
                error = calculateErrorChange(false, error, onErrorChange)
                return@onFocusChanged
            }
            error = calculateErrorChange(text.text.isEmpty(), error, onErrorChange)
        },
        textStyle = TextStyle(color = darkColors().onBackground)
    )
}

@Composable
fun numEntryFun(
    title: String,
    required: Boolean,
    onErrorChange: (Boolean) -> Unit = {},
    onValueChange: (Double) -> Unit = {},
    initialVal: Double? = null,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth(),
    negativeAllowed: Boolean = true,
    customErrorCheck: (String) -> Boolean = { false },
) {
    fun correctText(text: String): String {
        var parsedText = text
        if (text.startsWith(".")) parsedText = "0$parsedText"
        if (text.endsWith(".")) parsedText = parsedText.removeSuffix(".")
        if (text.endsWith("-")) parsedText += 0
        return parsedText
    }

    var error by remember { mutableStateOf(initialVal == null && required) }
    onErrorChange(error)
    var text by remember {
        mutableStateOf(TextFieldValue(initialVal?.toString()?.removeSuffix(".0") ?: ""))
    }
    var first by remember { mutableStateOf(true) }
    TextField(
        value = text,
        onValueChange = {
            var newText = it.text
            var newSelection = it.selection
            if (newText.endsWith(".") && text.text.length > newText.length) {
                newText = newText.removeSuffix(".")
            }
            if (negativeAllowed) {
                if(newText == "0-") newText = "-"
                if (newText.startsWith("-.")) {
                    newText = "-0.${newText.removePrefix("-.")}"
                    newSelection = TextRange(newSelection.end + 1)
                }
                if(newText.length > 1) {
                    newText = newText[0] + newText.substring(1,newText.length).filter { character ->
                        character != '-'
                    }
                }
            } else {
                newText = newText.filter { character ->
                    character != '-'
                }
            }
            if (newText.startsWith('.')) {
                newText = "0$newText"
                newSelection = TextRange(newSelection.end + 1)
            }
            if (newText.isEmpty()) {
                newText = "0"
                newSelection = TextRange(newSelection.max + 1)
            }
            if (newText.startsWith("0") && newText.length > 1 && newText[1] != '.') {
                newText = newText.trimStart('0')
                if (newText.isEmpty()) {
                    newText = "0"
                }
            }
            var singlePoint = true
            var singleNegativeSign = true
            val isValid = newText.fold(true) { running, currentChar ->
                val valid =
                    running && (currentChar.isDigit()
                            || currentChar == ','
                            || (currentChar == '-' && negativeAllowed)
                            || (currentChar == '.' && singlePoint)
                            || (currentChar == '-' && singleNegativeSign))
                if (currentChar == '.') {
                    singlePoint = false
                }
                if (currentChar == '-') {
                    singleNegativeSign = false
                }
                return@fold valid
            }
            if (!isValid) return@TextField

            if (text.text != newText) {
                onValueChange(correctText(newText).filter { character ->
                    character != ','
                }.toDouble())
            }
            text = TextFieldValue(newText, newSelection, it.composition)
            if (required) {
                val newError = text.text.isEmpty() || customErrorCheck(text.text)
                error = calculateErrorChange(newError, error, onErrorChange)
            }
        },
        label = { Text(title) },
        singleLine = singleLine,
        isError = error && !first,
        trailingIcon = {
            if (required) {
                Icon(Icons.Default.Star, "Star")
            }
        },
        modifier = modifier.fillMaxWidth().onFocusChanged {
            val newText = if(!it.isFocused) {
                correctText(text.text)
            } else {
                text.text
            }
            text = TextFieldValue(newText, TextRange(text.text.length), text.composition)
            if (!required) return@onFocusChanged
            if (it.isFocused) {
                if(first) first = false
                error = calculateErrorChange(false, error, onErrorChange)
                return@onFocusChanged
            }
            error = calculateErrorChange(text.text.isEmpty(), error, onErrorChange)
        },
        textStyle = TextStyle(color = darkColors().onBackground)
    )
}

@Composable
fun zipEntryFun(
    title: String,
    required: Boolean,
    onErrorChange: (Boolean) -> Unit = {},
    onValueChange: (Int) -> Unit = {},
    initialVal: Int? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    var error by remember { mutableStateOf(initialVal == null && required) }
    onErrorChange(error)
    var text by remember {
        mutableStateOf(TextFieldValue(initialVal?.toString() ?: ""))
    }
    var first by remember { mutableStateOf(true) }
    TextField(
        value = text,
        onValueChange = {
            println(it.text)
            var newText = it.text
            var newSelection = it.selection
            if (newText.isEmpty()) {
                newText = "0"
                newSelection = TextRange(newSelection.max + 1)
            }
            if (newText.startsWith("0") && newText.length > 1 && newText[1] != '.') {
                newText = newText.trimStart('0')
                if (newText.isEmpty()) {
                    newText = "0"
                }
            }
            val isValid = newText.fold(true) { running, currentChar ->
                val valid =
                    running && currentChar.isDigit()
                return@fold valid
            }
            if (!isValid) return@TextField

            if (text.text != newText) {
                onValueChange(newText.filter { character ->
                    character.isDigit()
                }.toInt())
            }
            text = TextFieldValue(newText, newSelection, it.composition)
            if (required) {
                val newError = text.text.isEmpty() || text.text.length != 5
                error = calculateErrorChange(newError, error, onErrorChange)
            }
        },
        label = { Text(title) },
        singleLine = true,
        isError = error && !first,
        trailingIcon = {
            if (required) {
                Icon(Icons.Default.Star, "Star")
            }
        },
        modifier = modifier.fillMaxWidth().onFocusChanged {
            text = TextFieldValue(text.text, TextRange(text.text.length), text.composition)
            if (!required) return@onFocusChanged
            if (it.isFocused) {
                if(first) first = false
                error = calculateErrorChange(false, error, onErrorChange)
                return@onFocusChanged
            }
            error = calculateErrorChange(text.text.isEmpty(), error, onErrorChange)
        },
        textStyle = TextStyle(color = darkColors().onBackground)
    )
}

class DateFormatException(message: String) : Exception(message)

@Composable
fun dateEntryFun(
    title: String,
    required: Boolean,
    onErrorChange: (Boolean) -> Unit = {},
    onValueChange: (java.sql.Date?) -> Unit = {},
    initialTime: Long? = null,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {

    fun parseDateFromMillis(millisSinceEpoch: Long): String {
        return SimpleDateFormat("MM/dd/yyyy").format(java.util.Date(millisSinceEpoch))
    }

    var error by remember { mutableStateOf(initialTime == null && required) }
    onErrorChange(error)
    var text by remember {
        mutableStateOf(
            TextFieldValue(
                if (initialTime == null) {
                    "MM/DD/YYYY"
                } else {
                    parseDateFromMillis(initialTime)
                }
            )
        )
    }
    var first by remember { mutableStateOf(true) }
    var cursorPlacement by remember { mutableStateOf(0) }
    TextField(
        value = text,
        onValueChange = {
            var digits = it.text.filter { character ->
                character.isDigit()
            }

            cursorPlacement = when {
                digits.length > 4 -> digits.length + 2
                digits.length > 2 -> digits.length + 1
                else -> digits.length
            }

            val newTextValue = if (digits.isEmpty()) {
                error = calculateErrorChange(true, error, onErrorChange)
                TextFieldValue(
                    "MM/DD/YYYY", TextRange(cursorPlacement)
                )
            } else {
                digits = digits.padEnd(8, ' ')
                TextFieldValue(
                    digits.substring(0, 2) + "/" + digits.substring(2, 4) + "/" + digits.substring(4, 8),
                    TextRange(cursorPlacement)
                )
            }

            if (text.text != newTextValue.text) {
                val date = try {
                    if (required) {
                        error = calculateErrorChange(false, error, onErrorChange)
                    }
                    Date(
                        SimpleDateFormat("MM/dd/yyyy").parse(newTextValue.text).time
                    )
                } catch (e: ParseException) {
                    if (required) {
                        error = calculateErrorChange(true, error, onErrorChange)
                    }
                    null
                }
                if(date != null) {
                    onValueChange(
                        date
                    )
                }
            }
            text = newTextValue
        },
        label = { Text(title) },
        singleLine = singleLine,
        isError = error && !first,
        trailingIcon = {
            if (required) {
                Icon(Icons.Default.Star, "Star")
            }
        },
        modifier = modifier.fillMaxWidth().onFocusChanged {
            if (!required) return@onFocusChanged
            if (it.isFocused) {
                if(first) first = false
                error = calculateErrorChange(false, error, onErrorChange)
                return@onFocusChanged
            }
//            error = calculateErrorChange(!it.isFocused && (error || !first), error, onErrorChange)
        },
        textStyle = TextStyle(color = darkColors().onBackground)
    )
}
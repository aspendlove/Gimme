package components

import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import java.sql.Date
import java.text.ParseException
import java.text.SimpleDateFormat

class DateEntry(title: String, required: Boolean, initialValue: Date?) :
    Entry<Date>(
        title, required, true,
        if (initialValue != null) {
            SimpleDateFormat("MM/dd/yyyy").format(initialValue)
        } else {
            "MM/DD/YYYY"
        }
    ) {
    override fun interpretText(textValue: String): Pair<Date?, String> {
        return if(isError) {
            Pair(
                null,
                textValue
            )
        } else {
            Pair(
                Date(SimpleDateFormat("MM/dd/yyyy").parse(textValue).time),
                textValue
            )
        }
    }

    override fun onValueChange(new: TextFieldValue, old: TextFieldValue): TextFieldValue {
        val returnText: TextFieldValue
        var digits = new.text.filter { toFilter -> toFilter.isDigit() }
        val cursorPlacement = if (digits.length > 4) {
            digits.length + 2
        } else if (digits.length > 2) {
            digits.length + 1
        } else {
            digits.length
        }

        if (digits.isEmpty()) {
            returnText = TextFieldValue(
                "MM/DD/YYYY", TextRange(cursorPlacement), new.composition
            )
        } else {
            while (digits.length < 8) {
                digits += ' '
            }

            returnText = TextFieldValue(
                digits.substring(0, 2) + "/" + digits.substring(2, 4) + "/" + digits.substring(4, 8),
                TextRange(cursorPlacement),
                new.composition
            )
        }

        return returnText
    }

    override fun isEntryEmpty(text: String): Boolean {
        return text.isEmpty() || text == " /  /    " || text == "MM/DD/YYYY"
    }

    override fun checkValueErrorStatus(new: TextFieldValue, old: TextFieldValue): Boolean {
        return checkError(new.text)
    }

    override fun checkFocusErrorStatus(new: FocusState, first: Boolean, text: String): Boolean {
        return checkError(text)
    }

    private fun checkError(text: String): Boolean {
        return try {
            SimpleDateFormat("MM/dd/yyyy").parse(text)
            false
        } catch (e: ParseException) {
            true
        }
    }
}

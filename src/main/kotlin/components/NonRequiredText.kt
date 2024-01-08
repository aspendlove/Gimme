package components
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

class NonRequiredText(private var title: String, private var initialText: String = ""):
    ComponentBase(Modifier.fillMaxWidth()){
    private var _text = initialText
    val result: String
        get() = _text

    fun changeText(newText: String) {
        _text = newText
    }

    @Composable
    @Preview
    override fun compose() {
        var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(initialText))
        }

        TextField(
            value = text,
            onValueChange = {
                text = it
                _text = text.text
            },
            label = { Text(title) },
            singleLine = true,
            modifier = modifier.fillMaxWidth(),
            textStyle = TextStyle(color = darkColors().onBackground)
        )
    }
}


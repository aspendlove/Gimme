
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

class NonRequiredTextMultiline(private var title: String) {
    private var _text = ""
    val result: String
        get() = _text

    var modifier:Modifier = Modifier

    @Composable
    @Preview
    fun compose() {
        var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }

        TextField(
            value = text,
            onValueChange = {
                text = it
                _text = text.text
            },
            label = { Text(title) },
            singleLine = false,
            modifier = modifier.fillMaxWidth()
        )
    }
}

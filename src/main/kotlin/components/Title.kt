package components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Title(private val text: String) : ComponentBase(Modifier.padding(10.dp)) {
    @Composable
    override fun compose() {
        Text(text, modifier, color = darkColors().onBackground, fontSize = 40.sp, textAlign = TextAlign.Center)
    }
}

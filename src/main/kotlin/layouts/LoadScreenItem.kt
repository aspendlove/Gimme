package layouts

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.Body
import components.CustomButton

class LoadScreenItem<T>(val id: Int, private val name: String, private val item: T, private val onPress: (T) -> Unit) {
    @Composable
    fun compose() {
        val loadButton = CustomButton({
            onPress(item)
        },"Load")
        Row {
            Body(name).compose()
            loadButton.compose()
        }
    }
}

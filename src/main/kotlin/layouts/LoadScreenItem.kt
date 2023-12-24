package layouts

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import components.Body
import components.CustomButton

class LoadScreenItem<T>(val id: Int,
                        private val name: String,
                        private val item: T,
                        private val onLoad: (T) -> Unit,
                        private val onDelete: () -> Unit) {
    @Composable
    fun compose() {
        val loadButton = CustomButton({
            onLoad(item)
        }, "Load")
        val deleteButton = CustomButton({
            onDelete()
        }, "Delete")
        Row {
            Body(name).compose()
            loadButton.compose()
            deleteButton.compose()
        }
    }
}

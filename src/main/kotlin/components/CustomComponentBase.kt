package components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

abstract class CustomComponentBase(private var _modifier: Modifier) {

    fun addModifier(other: Modifier) {
        _modifier = _modifier.then(other)
    }
    var modifier: Modifier
        get() {
            return _modifier
        }
        set(value){
            _modifier = value;
        }

    @Composable
    abstract fun compose()
}
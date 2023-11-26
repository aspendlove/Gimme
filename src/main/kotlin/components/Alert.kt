@file:OptIn(ExperimentalMaterialApi::class)

package components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.PopupAlertDialogProvider.AlertDialog

import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class Alert(private val alertText: String): CustomComponentBase(Modifier) {
    @Composable
    override fun compose() {
        AlertDialog(onDismissRequest = {}, shape = RoundedCornerShape(4.dp), Modifier) {
            Text(alertText)
        }
    }

}

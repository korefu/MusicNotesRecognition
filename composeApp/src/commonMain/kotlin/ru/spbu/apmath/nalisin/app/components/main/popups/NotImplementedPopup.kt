package ru.spbu.apmath.nalisin.app.components.main.popups

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * @author s.nalisin
 */
@Composable
fun NotImplementedPopup(show: Boolean, onOkClick: () -> Unit) {
    if (show) {
        AlertDialog(
            shape = RoundedCornerShape(12.dp),
            onDismissRequest = onOkClick, confirmButton = {
                Button(onClick = onOkClick) { Text("OK") }
            },
            text = {
                Text("Функция пока в разработке")
            }
        )
    }
}
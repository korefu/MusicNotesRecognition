package ru.spbu.apmath.nalisin.app.components.main.common

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

/**
 * @author s.nalisin
 */
@Composable
fun RowScope.MaxRelWidthButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .weight(1f),
        onClick = onClick,
        shape = CircleShape,
    ) {
        Text(text = text, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleSmall)
    }
}
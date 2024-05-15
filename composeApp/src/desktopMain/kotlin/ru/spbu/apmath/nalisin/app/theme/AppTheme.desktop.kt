package ru.spbu.apmath.nalisin.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * @author s.nalisin
 */
@Composable
actual fun AppTheme(darkTheme: Boolean, dynamicColor: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkScheme else lightScheme,
        typography = AppTypography,
        content = content
    )
}
package ru.spbu.apmath.nalisin.app.components.main.fileopen

import androidx.compose.runtime.Composable

/**
 * @author s.nalisin
 */
@Composable
expect fun FileOpener(
    show: Boolean,
    midi: ByteArray,
    onFileOpened: () -> Unit,
)

@Composable
expect fun FileOpener(
    show: Boolean,
    musicXml: String,
    onFileOpened: () -> Unit,
)
package ru.spbu.apmath.nalisin.app.components.main.fileexport

import androidx.compose.runtime.Composable

/**
 * @author s.nalisin
 */
@Composable
actual fun FileExporter(
    show: Boolean,
    midi: ByteArray,
    initialDirectory: String?,
    title: String,
    onFileExported: () -> Unit
) {
}

@Composable
actual fun FileExporter(
    show: Boolean,
    musicXml: String,
    initialDirectory: String?,
    title: String,
    onFileExported: () -> Unit
) {
}
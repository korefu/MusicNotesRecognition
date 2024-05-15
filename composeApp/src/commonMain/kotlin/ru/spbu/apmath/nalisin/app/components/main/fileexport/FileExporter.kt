package ru.spbu.apmath.nalisin.app.components.main.fileexport

import androidx.compose.runtime.Composable

/**
 * @author s.nalisin
 */
@Composable
expect fun FileExporter(
    show: Boolean,
    midi: ByteArray,
    initialDirectory: String? = null,
    title: String = "musicFile.mid",
    onFileExported: () -> Unit,
)

@Composable
expect fun FileExporter(
    show: Boolean,
    musicXml: String,
    initialDirectory: String? = null,
    title: String = "musicFile.musicxml",
    onFileExported: () -> Unit,
)
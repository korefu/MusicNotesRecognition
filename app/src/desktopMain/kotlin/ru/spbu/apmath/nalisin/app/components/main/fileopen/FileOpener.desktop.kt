package ru.spbu.apmath.nalisin.app.components.main.fileopen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import java.awt.Desktop
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter


/**
 * @author s.nalisin
 */
@Composable
actual fun FileOpener(
    show: Boolean,
    midi: ByteArray,
    onFileOpened: () -> Unit
) {
    LaunchedEffect(show) {
        if (show) {
            val file = File("output.mid")
            FileOutputStream(file).use { stream ->
                stream.write(midi)
            }
            val dt = Desktop.getDesktop()
            dt.open(file)
            onFileOpened()
        }
    }
}

@Composable
actual fun FileOpener(
    show: Boolean,
    musicXml: String,
    onFileOpened: () -> Unit
) {
    LaunchedEffect(show) {
        if (show) {
            val file = File("output.musicxml")
            BufferedWriter(FileWriter(file)).use { out ->
                out.write(musicXml)
            }
            val dt = Desktop.getDesktop()
            dt.open(file)
            onFileOpened()
        }
    }
}
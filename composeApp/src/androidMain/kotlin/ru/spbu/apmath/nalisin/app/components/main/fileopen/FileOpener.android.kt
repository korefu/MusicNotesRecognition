package ru.spbu.apmath.nalisin.app.components.main.fileopen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * @author s.nalisin
 */
@Composable
actual fun FileOpener(
    show: Boolean,
    midi: ByteArray,
    onFileOpened: () -> Unit
) {
    FileOpener(
        show = show,
        byteArray = midi,
        title = "output.mid",
        type = "audio/midi",
        onFileOpened = onFileOpened
    )
}

@Composable
actual fun FileOpener(
    show: Boolean,
    musicXml: String,
    onFileOpened: () -> Unit
) {
    FileOpener(
        show = show,
        byteArray = musicXml.toByteArray(),
        title = "output.musicxml",
        type = "text/xml",
        onFileOpened = onFileOpened
    )
}

@Composable
private fun FileOpener(
    show: Boolean,
    byteArray: ByteArray,
    title: String,
    type: String,
    onFileOpened: () -> Unit
) {
    val context = LocalContext.current
    val appDirectory = context.filesDir
    LaunchedEffect(show) {
        if (show) {

            val file = File("${appDirectory.path}/$title")
            FileOutputStream(file).use { stream ->
                stream.write(byteArray)
            }
            val midiUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(midiUri, type)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No suitable app found to open file", Toast.LENGTH_LONG).show()
            }
            onFileOpened()
        }
    }
}
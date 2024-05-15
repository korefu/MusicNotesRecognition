package ru.spbu.apmath.nalisin.app.components.main.fileexport

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
actual fun FileExporter(
    show: Boolean,
    midi: ByteArray,
    initialDirectory: String?,
    title: String,
    onFileExported: () -> Unit
) {
    FileExporter(
        show = show,
        byteArray = midi,
        title = title,
        type = "audio/midi",
        onFileExported = onFileExported,
    )
}

@Composable
actual fun FileExporter(
    show: Boolean,
    musicXml: String,
    initialDirectory: String?,
    title: String,
    onFileExported: () -> Unit
) {
    FileExporter(
        show = show,
        byteArray = musicXml.toByteArray(),
        title = title,
        type = "text/xml",
        onFileExported = onFileExported,
    )
}

@Composable
private fun FileExporter(
    show: Boolean,
    byteArray: ByteArray,
    title: String,
    type: String,
    onFileExported: () -> Unit
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
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                setDataAndType(midiUri, type)
                putExtra(Intent.EXTRA_TITLE, title)
            }
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No suitable app found to open MIDI file", Toast.LENGTH_LONG).show()
            }
            onFileExported()
        }
    }
}
package ru.spbu.apmath.nalisin.app.components.main.fileimport

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.spbu.apmath.nalisin.app.theme.AppTheme

@Composable
fun FileImportContent(
    modifier: Modifier = Modifier,
    fileImport: FileImport,
) {
    val filePicker by fileImport.state.subscribeAsState()
    FilePicker(show = filePicker.showFilePicker, fileExtensions = fileImport.fileTypes) { platformFile ->
        fileImport.fileReceived(platformFile)
    }
    Button(
        modifier = modifier,
        onClick = fileImport::importFileButtonCLicked,
    ) {
        Icon(modifier = Modifier.padding(vertical = 4.dp), imageVector = Icons.Default.UploadFile, contentDescription = null)
        Text(modifier = Modifier.padding(4.dp, 4.dp, 0.dp, 4.dp), text = "Импорт wave файла")
    }
}

@Preview
@Composable
fun FileImportContentPreview() {
    AppTheme {
        FileImportContent(
            fileImport = FileImport(
                componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
                getAudioFormatUseCase = { throw NotImplementedError() },
            )
        )
    }
}
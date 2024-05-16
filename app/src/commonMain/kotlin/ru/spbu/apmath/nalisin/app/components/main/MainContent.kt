package ru.spbu.apmath.nalisin.app.components.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.spbu.apmath.nalisin.app.components.main.common.MaxRelWidthButton
import ru.spbu.apmath.nalisin.app.components.main.convertersettings.ConverterSettingsContent
import ru.spbu.apmath.nalisin.app.components.main.fileexport.FileExporter
import ru.spbu.apmath.nalisin.app.components.main.fileimport.FileImportContent
import ru.spbu.apmath.nalisin.app.components.main.fileopen.FileOpener
import ru.spbu.apmath.nalisin.app.components.main.popups.NotImplementedPopup
import ru.spbu.apmath.nalisin.app.components.main.recorder.RecorderContent
import ru.spbu.apmath.nalisin.app.theme.AppTheme
import ru.spbu.apmath.nalisin.common_entities.MidiNote

@Composable
fun MainContent(
    component: MainComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()
    val recorder by component.recorder.subscribeAsState()
    val converterState by component.converterState.subscribeAsState()
    val fileImport by component.fileImport.subscribeAsState()
    val converterSettings by component.converterSettings.subscribeAsState()

    AppTheme {
        Surface(modifier = modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Greeting()
                FileImportContent(fileImport = fileImport)
                ConverterSettingsContent(converterSettings = converterSettings)
                RecorderContent(recorder = recorder)
                ConverterContent(
                    showFileOpen = state.showFileOpen,
                    showFileExport = state.showFileExport,
                    converterState = converterState,
                    onFileExported = component::onFileExported,
                    onFileOpened = component::onFileOpened,
                    onExportClick = component::onExportClick,
                    onOpenClick = component::onOpenClick,
                    onMidiCheckboxClicked = component::onMidiCheckboxClicked,
                    onMusicXmlCheckboxClicked = component::onMusicXmlCheckboxClicked,
                    fileType = state.fileType,
                )
            }
            NotImplementedPopup(
                show = state.showNotImplementedPopup,
                onOkClick = component::onNotImplementedPopupClosed
            )
        }
    }
}

@Composable
private fun ColumnScope.Greeting() {
    Text(
        text = "Получение нотной записи",
        modifier = Modifier.align(Alignment.CenterHorizontally),
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
private fun ConverterContent(
    fileType: MainComponent.FileType,
    showFileExport: Boolean,
    showFileOpen: Boolean,
    converterState: ConverterState,
    onExportClick: (List<MidiNote>) -> Unit,
    onOpenClick: (List<MidiNote>) -> Unit,
    onFileExported: () -> Unit,
    onFileOpened: () -> Unit,
    onMusicXmlCheckboxClicked: (Boolean) -> Unit,
    onMidiCheckboxClicked: (Boolean) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        when (converterState) {
            is ConverterState.MidiNotesReceived -> {
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp).fillMaxWidth()) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "Данные о нотах получены",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    CheckboxWithText(
                        checked = fileType == MainComponent.FileType.MusicXml,
                        onCheckedChange = onMusicXmlCheckboxClicked,
                        text = "MusicXml"
                    )
                    CheckboxWithText(
                        checked = fileType == MainComponent.FileType.MIDI,
                        onCheckedChange = onMidiCheckboxClicked,
                        text = "MIDI"
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.width(IntrinsicSize.Min)
                    ) {
                        MaxRelWidthButton(text = "Экспорт", onClick = { onExportClick(converterState.midiNotes) })
                        MaxRelWidthButton(text = "Открыть", onClick = { onOpenClick(converterState.midiNotes) })
                    }
                    when (val tempState = converterState as ConverterState.MidiNotesReceived) {
                        is ConverterState.MidiNotesReceived.MidiReceived -> {
                            FileExporter(
                                show = showFileExport,
                                midi = tempState.midi,
                                onFileExported = onFileExported
                            )
                            FileOpener(
                                show = showFileOpen,
                                midi = tempState.midi,
                                onFileOpened = onFileOpened
                            )
                        }

                        is ConverterState.MidiNotesReceived.MusicXmlReceived -> {
                            FileExporter(
                                show = showFileExport,
                                musicXml = tempState.musicXml,
                                onFileExported = onFileExported
                            )
                            FileOpener(
                                show = showFileOpen,
                                musicXml = tempState.musicXml,
                                onFileOpened = onFileOpened
                            )
                        }

                        is ConverterState.MidiNotesReceived.Init -> Unit
                    }
                }
            }

            is ConverterState.Initial -> Unit
            is ConverterState.Processing -> Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                text = "Обработка...",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CheckboxWithText(checked: Boolean, onCheckedChange: (Boolean) -> Unit, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Text(text = text)
    }
}

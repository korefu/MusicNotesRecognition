package ru.spbu.apmath.nalisin.app.components.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.spbu.apmath.nalisin.app.components.main.MainComponent.FileType
import ru.spbu.apmath.nalisin.app.components.main.MainComponent.State
import ru.spbu.apmath.nalisin.app.components.main.convertersettings.ConverterSettings
import ru.spbu.apmath.nalisin.app.components.main.fileimport.FileImport
import ru.spbu.apmath.nalisin.app.components.main.recorder.Recorder
import ru.spbu.apmath.nalisin.app.components.main.recorder.RecorderImpl
import ru.spbu.apmath.nalisin.app.components.main.recorder.VoiceRecorder
import ru.spbu.apmath.nalisin.audio_to_midi_converter.MidiConverter
import ru.spbu.apmath.nalisin.audio_to_midi_converter.MidiNotesComposer
import ru.spbu.apmath.nalisin.audio_to_midi_converter.MusicXmlConverter
import ru.spbu.apmath.nalisin.common_entities.MidiNote
import ru.spbu.apmath.nalisin.common_entities.MusicFile
import ru.spbu.apmath.nalisin.common_utils.GetAudioFormatUseCase
import kotlin.coroutines.CoroutineContext

interface MainComponent {

    fun onExportClick(midiNotes: List<MidiNote>)
    fun onOpenClick(midiNotes: List<MidiNote>)

    fun onFileExported()
    fun onFileOpened()
    fun onNotImplementedPopupClosed()
    fun onMidiCheckboxClicked(value: Boolean)
    fun onMusicXmlCheckboxClicked(value: Boolean)

    val state: Value<State>
    val converterSettings: Value<ConverterSettings>
    val recorder: Value<Recorder>
    val fileImport: Value<FileImport>
    val converterState: Value<ConverterState>

    data class State(
        val showFileExport: Boolean = false,
        val showFileOpen: Boolean = false,
        val fileType: FileType = FileType.MusicXml,
        val showNotImplementedPopup: Boolean = false,
    )

    sealed interface FileType {
        object MIDI : FileType
        object MusicXml : FileType
    }
}

class MainComponentImpl(
    componentContext: ComponentContext,
    getAudioFormatUseCase: GetAudioFormatUseCase,
    private val midiNotesComposer: MidiNotesComposer,
    private val musicXmlConverter: MusicXmlConverter,
    private val midiConverter: MidiConverter,
    voiceRecorder: VoiceRecorder,
    mainContext: CoroutineContext = Dispatchers.Main,
    private val ioContext: CoroutineContext = Dispatchers.IO,
) : MainComponent, ComponentContext by componentContext {

    private val _converterState: MutableValue<ConverterState> = MutableValue(ConverterState.Initial)
    override val converterState: Value<ConverterState> = _converterState
    private val _state = MutableValue(State())
    override val state: Value<State> = _state

    private val scope = coroutineScope(mainContext + SupervisorJob())

    override fun onExportClick(midiNotes: List<MidiNote>) {
        _converterState.update { ConverterState.Processing }
        scope.launch(ioContext) {
            when (_state.value.fileType) {
                FileType.MIDI -> {
                    val midi = midiConverter.convertToMidi(
                        midiNotes = midiNotes,
                        settings = converterSettings.value.state.value.settings,
                    )
                    _converterState.update {
                        ConverterState.MidiNotesReceived.MidiReceived(
                            midiNotes = midiNotes,
                            midi = midi
                        )
                    }
                }

                FileType.MusicXml -> {
                    val musicXml = musicXmlConverter.convertToMusicXml(
                        midiNotes = midiNotes,
                        settings = converterSettings.value.state.value.settings,
                    )
                    _converterState.update {
                        ConverterState.MidiNotesReceived.MusicXmlReceived(
                            midiNotes = midiNotes,
                            musicXml = musicXml,
                        )
                    }
                }
            }
            _state.update { it.copy(showFileExport = true) }
        }
    }

    override fun onOpenClick(midiNotes: List<MidiNote>) {
        _converterState.update { ConverterState.Processing }
        scope.launch(ioContext) {
            when (_state.value.fileType) {
                FileType.MIDI -> {
                    val midi = midiConverter.convertToMidi(
                        midiNotes = midiNotes,
                        settings = converterSettings.value.state.value.settings,
                    )
                    _converterState.update {
                        ConverterState.MidiNotesReceived.MidiReceived(
                            midiNotes = midiNotes,
                            midi = midi
                        )
                    }
                }

                FileType.MusicXml -> {
                    val musicXml = musicXmlConverter.convertToMusicXml(
                        midiNotes = midiNotes,
                        settings = converterSettings.value.state.value.settings,
                    )
                    _converterState.update {
                        ConverterState.MidiNotesReceived.MusicXmlReceived(
                            midiNotes = midiNotes,
                            musicXml = musicXml,
                        )
                    }
                }
            }
            _state.update { it.copy(showFileOpen = true) }
        }
    }

    override fun onFileExported() {
        _state.update { it.copy(showFileExport = false) }
    }

    override fun onFileOpened() {
        _state.update { it.copy(showFileOpen = false) }
    }

    override fun onNotImplementedPopupClosed() {
        _state.update { it.copy(showNotImplementedPopup = false) }
    }

    override fun onMidiCheckboxClicked(value: Boolean) {
        _state.update { it.copy(fileType = FileType.MIDI) }
    }

    override fun onMusicXmlCheckboxClicked(value: Boolean) {
        _state.update { it.copy(fileType = FileType.MusicXml) }
    }

    override val converterSettings: Value<ConverterSettings> = MutableValue(
        ConverterSettings(componentContext = componentContext)
    )
    override val recorder: Value<Recorder> = MutableValue(
        RecorderImpl(
            componentContext = componentContext,
            voiceRecorder = voiceRecorder,
            onMusicFileRecorded = ::onMusicFileImported,
        )
    )
    override val fileImport: Value<FileImport> = MutableValue(
        FileImport(
            componentContext = componentContext,
            onMusicFileImported = ::onMusicFileImported,
            getAudioFormatUseCase = getAudioFormatUseCase,
        )
    )

    private fun onMusicFileImported(file: MusicFile?) {
        file?.let {
            _converterState.update { ConverterState.Processing }
            scope.launch(ioContext) {
                val midiNotes = midiNotesComposer.composeMidiNotes(
                    musicFile = file,
                    settings = converterSettings.value.state.value.settings,
                )
                _converterState.update { ConverterState.MidiNotesReceived.Init(midiNotes) }
            }
        }
    }
}

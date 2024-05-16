package ru.spbu.apmath.nalisin.app.di

import com.arkivanov.decompose.ComponentContext
import com.example.musicxml_writer.MusicXmlCreator
import com.example.musicxml_writer.MusicXmlCreatorImpl
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import ru.spbu.apmath.nalisin.app.FFTFrequencyRecognizer
import ru.spbu.apmath.nalisin.app.components.main.MainComponent
import ru.spbu.apmath.nalisin.app.components.main.MainComponentImpl
import ru.spbu.apmath.nalisin.app.components.main.recorder.VoiceRecorder
import ru.spbu.apmath.nalisin.app.components.main.recorder.VoiceRecorderImpl
import ru.spbu.apmath.nalisin.audio_splitter.AudioSplitter
import ru.spbu.apmath.nalisin.audio_splitter.AudioSplitterImpl
import ru.spbu.apmath.nalisin.audio_to_midi_converter.MidiConverter
import ru.spbu.apmath.nalisin.audio_to_midi_converter.MidiConverterImpl
import ru.spbu.apmath.nalisin.audio_to_midi_converter.MidiNotesComposer
import ru.spbu.apmath.nalisin.audio_to_midi_converter.MidiNotesComposerImpl
import ru.spbu.apmath.nalisin.audio_to_midi_converter.MusicXmlConverter
import ru.spbu.apmath.nalisin.audio_to_midi_converter.MusicXmlConverterImpl
import ru.spbu.apmath.nalisin.common_utils.GetAudioFormatUseCase
import ru.spbu.apmath.nalisin.common_utils.GetAudioFormatUseCaseImpl
import ru.spbu.apmath.nalisin.frequency_recognition_api.FrequencyRecognizer
import ru.spbu.apmath.nalisin.loudness_analyzer.LoudnessAnalyzer
import ru.spbu.apmath.nalisin.loudness_analyzer.LoudnessAnalyzerImpl

@Component
abstract class MainDiComponent(@get:Provides protected val componentContext: ComponentContext) {

    abstract val mainComponent: MainComponent

    @Provides
    protected fun mainComponent(
        getAudioFormatUseCase: GetAudioFormatUseCase,
        voiceRecorder: VoiceRecorder,
        midiNotesComposer: MidiNotesComposer,
        musicXmlConverter: MusicXmlConverter,
        midiConverter: MidiConverter,
    ): MainComponent = MainComponentImpl(
        componentContext = componentContext,
        getAudioFormatUseCase = getAudioFormatUseCase,
        musicXmlConverter = musicXmlConverter,
        voiceRecorder = voiceRecorder,
        midiConverter = midiConverter,
        midiNotesComposer = midiNotesComposer,
    )

    protected val GetAudioFormatUseCaseImpl.bind: GetAudioFormatUseCase
        @Provides get() = this

    protected val AudioSplitterImpl.bind: AudioSplitter
        @Provides get() = this

    protected val FFTFrequencyRecognizer.bind: FrequencyRecognizer
        @Provides get() = this

    protected val VoiceRecorderImpl.bind: VoiceRecorder
        @Provides get() = this

    protected val MusicXmlCreatorImpl.bind: MusicXmlCreator
        @Provides get() = this

    protected val MidiNotesComposerImpl.bind: MidiNotesComposer
        @Provides get() = this

    protected val MusicXmlConverterImpl.bind: MusicXmlConverter
        @Provides get() = this

    protected val MidiConverterImpl.bind: MidiConverter
        @Provides get() = this

    protected val LoudnessAnalyzerImpl.bind : LoudnessAnalyzer
        @Provides get() = this
}

@KmpComponentCreate
expect fun createMainDiComponent(componentContext: ComponentContext): MainDiComponent
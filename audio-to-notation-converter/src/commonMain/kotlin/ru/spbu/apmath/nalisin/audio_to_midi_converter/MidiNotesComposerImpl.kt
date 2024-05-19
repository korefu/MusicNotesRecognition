package ru.spbu.apmath.nalisin.audio_to_midi_converter

import me.tatarka.inject.annotations.Inject
import ru.spbu.apmath.nalisin.audio_splitter.AudioSplitter
import ru.spbu.apmath.nalisin.audio_to_midi_converter.utils.MedianFilter.applyMedianFilter
import ru.spbu.apmath.nalisin.audio_to_midi_converter.utils.NotesMerger.mergeNotes
import ru.spbu.apmath.nalisin.audio_to_midi_converter.utils.NotesSmoother.postProcessNotes
import ru.spbu.apmath.nalisin.audio_to_midi_converter.utils.parallelMap
import ru.spbu.apmath.nalisin.audio_to_midi_converter.utils.parallelMapIndexed
import ru.spbu.apmath.nalisin.common_entities.MidiNote
import ru.spbu.apmath.nalisin.common_entities.MusicFile
import ru.spbu.apmath.nalisin.common_entities.TimeFrequency
import ru.spbu.apmath.nalisin.common_utils.GetAudioFormatUseCase
import ru.spbu.apmath.nalisin.frequency_recognition_api.FrequencyRecognizer
import ru.spbu.apmath.nalisin.loudness_analyzer.LoudnessAnalyzer
import java.io.File
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * @author s.nalisin
 */
@Inject
class MidiNotesComposerImpl(
    private val getAudioFormatUseCase: GetAudioFormatUseCase,
    private val audioSplitter: AudioSplitter,
    private val frequencyRecognizer: FrequencyRecognizer,
    private val loudnessAnalyzer: LoudnessAnalyzer,
) : MidiNotesComposer {

    override suspend fun composeMidiNotes(audioFilePath: String, settings: Settings): List<MidiNote> {
        val musicFile = MusicFile(
            audioData = File(audioFilePath).readBytes(),
            format = getAudioFormatUseCase(audioFilePath),
        )
        return composeMidiNotes(musicFile = musicFile, settings = settings)
    }

    override suspend fun composeMidiNotes(
        musicFile: MusicFile,
        settings: Settings
    ): List<MidiNote> {
        val frequencies = getFrequencies(musicFile = musicFile, settings = settings)
        return composeMidiNotes(frequencies = frequencies, settings = settings)
    }

    private suspend fun getFrequencies(
        musicFile: MusicFile,
        settings: Settings
    ): List<TimeFrequency> {
        val audioFragments = audioSplitter.splitAudio(
            musicFile = musicFile,
            durationInMillis = settings.fragmentDurationInMillis,
        )
        val frequencies = audioFragments
                .parallelMapIndexed { index, audioFragmentData ->
                    val loudnessInDb =
                        loudnessAnalyzer.getAverageLoudness(MusicFile(audioFragmentData, musicFile.format))
                    val time = settings.fragmentDurationInMillis * index
                    if (loudnessInDb > LOUDNESS_THRESHOLD) {
                        TimeFrequency(
                            time = time,
                            frequency = audioFragmentData.let {
                                frequencyRecognizer.recognizeFrequency(
                                    musicFile = MusicFile(
                                        audioData = audioFragmentData,
                                        format = musicFile.format,
                                    )
                                )
                            }
                        )
                    } else {
                        TimeFrequency(time = time, frequency = null)
                    }
                }
        return frequencies
    }

    override suspend fun composeMidiNotes(frequencies: List<TimeFrequency>, settings: Settings): List<MidiNote> {
        return getMidiNotes(frequencies = frequencies, settings = settings)
    }

    private suspend fun getMidiNotes(
        frequencies: List<TimeFrequency>,
        settings: Settings,
    ): List<MidiNote> {
        val notes = frequencies.mapToNotes(settings.bpm)
        val resultNotes = notes
            .applyMedianFilter(windowSize = settings.medianFilterWindowSize)
            .mergeNotes(bpm = settings.bpm)
            .dropFirstPause()
            .postProcessNotes(minNoteDurationThreshold = settings.minDurationThreshold) // todo должен быть пропорционален окну, сильно влияет на точность
        return resultNotes
    }

    private fun List<MidiNote>.dropFirstPause(): List<MidiNote> {
        return if (this.firstOrNull() is MidiNote.Rest) this.drop(1) else this
    }

    // todo если уточнять тональность и убирать неиспользуемые в тональности ноты, то точность бы повысилась
    private fun mapFrequencyToNote(
        frequency: Double?,
        durationInMillis: Long,
        bpm: Int,
        deviation: Double = 0.0,
    ): MidiNote {
        val wholeNoteDurationInMillis = 60.0 / bpm * 1000.0 / QUARTER_TONE_DURATION
        val durationInBeats = durationInMillis / wholeNoteDurationInMillis
        if (frequency == null) return MidiNote.Rest(durationInBeats)
        val noteValue = frequencyToMidi(frequency, deviation).roundToInt()
        return MidiNote.Melodic(noteValue, durationInBeats)
    }

    private fun frequencyToMidi(frequency: Double, deviation: Double = 1.0): Double {
        val a4Frequency = 440.0 * deviation // частота La4
        val midiA4 = 69 // номер ноты La4 в MIDI

        val noteNum = 12 * (log2(frequency / a4Frequency)) + midiA4
        return noteNum
    }

    // неоптимальная попытка сместить частоту всех нот, если инструмент неточно играет
    private suspend fun findOptimalFrequencyDeviation(
        frequencies: List<Double?>,
    ): Double {
        val maxDeviationMultiplier = 2.0.pow(1.0 / 24)
        val steps = 20

        return (-steps..steps).parallelMap { step ->
            val deviation = maxDeviationMultiplier.pow(step.toDouble() / steps)
            var totalError = 0.0
            for (frequency in frequencies) {
                if (frequency == null) continue
                val adjustedMidi = frequencyToMidi(frequency, deviation)
                totalError += abs(adjustedMidi - adjustedMidi.roundToInt())
            }
            deviation to totalError
        }.minByOrNull { it.second }?.first ?: 0.0
    }

    private suspend fun List<TimeFrequency>.mapToNotes(bpm: Int): List<MidiNote> {
        val optimalDeviation =
            findOptimalFrequencyDeviation(frequencies = this.map { it.frequency })
        val notes = mutableListOf<MidiNote>()
        var previousNoteTimestamp: Long = 0
        this.forEach {
            notes.add(
                mapFrequencyToNote(
                    frequency = it.frequency,
                    durationInMillis = it.time - previousNoteTimestamp,
                    deviation = optimalDeviation,
                    bpm = bpm
                ),
            )
            previousNoteTimestamp = it.time
        }
        return notes
    }

    private companion object {
        private const val QUARTER_TONE_DURATION = 0.25
        private const val LOUDNESS_THRESHOLD = -40.0
    }
}
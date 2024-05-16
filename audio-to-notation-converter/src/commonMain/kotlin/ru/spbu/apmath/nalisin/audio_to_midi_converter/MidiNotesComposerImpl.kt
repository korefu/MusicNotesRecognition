package ru.spbu.apmath.nalisin.audio_to_midi_converter

import me.tatarka.inject.annotations.Inject
import ru.spbu.apmath.nalisin.audio_splitter.AudioSplitter
import ru.spbu.apmath.nalisin.audio_to_midi_converter.utils.MedianFilter.applyMedianFilter
import ru.spbu.apmath.nalisin.audio_to_midi_converter.utils.NotesMerger.mergeNotes
import ru.spbu.apmath.nalisin.audio_to_midi_converter.utils.NotesSmoother.postProcessNotes
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

    override fun composeMidiNotes(audioFilePath: String, settings: Settings): List<MidiNote> {
        val musicFile = MusicFile(
            audioData = File(audioFilePath).readBytes(),
            format = getAudioFormatUseCase(audioFilePath),
        )
        return composeMidiNotes(musicFile = musicFile, settings = settings)
    }

    override fun composeMidiNotes(
        musicFile: MusicFile,
        settings: Settings
    ): List<MidiNote> {
        val frequencies = getFrequencies(musicFile = musicFile, settings = settings)
        return composeMidiNotes(frequencies = frequencies, settings = settings)
    }

    private fun getFrequencies(
        musicFile: MusicFile,
        settings: Settings
    ): List<TimeFrequency> {
        val audioFragments = audioSplitter.splitAudio(
            musicFile = musicFile,
            durationInMillis = settings.fragmentDurationInMillis,
        )
        val frequencies = audioFragments
            .mapIndexed { index, audioFragmentData ->
                val loudnessInDb = loudnessAnalyzer.getAverageLoudness(MusicFile(audioFragmentData, musicFile.format))
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

    override fun composeMidiNotes(frequencies: List<TimeFrequency>, settings: Settings): List<MidiNote> {
        return getMidiNotes(frequencies = frequencies, settings = settings)
    }

    private fun getMidiNotes(
        frequencies: List<TimeFrequency>,
        settings: Settings,
    ): List<MidiNote> {
        val notes = frequencies.mapToNotes(settings.bpm)
        val resultNotes = notes
            .applyMedianFilter(windowSize = settings.medianFilterWindowSize)
            .mergeNotes(bpm = settings.bpm)
            .postProcessNotes(minDurationThreshold = settings.minDurationThreshold) // todo должен быть пропорционален окну, сильно влияет на точность
        return resultNotes
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
    private fun findOptimalFrequencyDeviation(
        frequencies: List<Double?>,
        deviationRange: Double = 0.5 // измеряется в полутонах
    ): Double {
        val maxDeviationMultiplier = 2.0.pow(1.0 / 24)

        var minError = Double.MAX_VALUE
        var optimalDeviation = 0.0
        val steps = 10

        for (i in -steps..steps) {
            val deviation = maxDeviationMultiplier.pow(i.toDouble() / steps)
            var totalError = 0.0
            for (frequency in frequencies) {
                if (frequency == null) continue
                val adjustedMidi = frequencyToMidi(frequency, deviation)
                totalError += abs(adjustedMidi - adjustedMidi.roundToInt())
            }

            if (totalError < minError) {
                minError = totalError
                optimalDeviation = deviation
            }
        }

        return optimalDeviation
    }

    private fun List<TimeFrequency>.mapToNotes(bpm: Int): List<MidiNote> {
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
        private const val LOUDNESS_THRESHOLD = -35.0
    }
}
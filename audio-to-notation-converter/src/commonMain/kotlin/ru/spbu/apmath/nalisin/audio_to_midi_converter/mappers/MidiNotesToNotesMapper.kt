package ru.spbu.apmath.nalisin.audio_to_midi_converter.mappers

import ru.spbu.apmath.nalisin.audio_to_midi_converter.utils.NotesDivider.divideNotes
import ru.spbu.apmath.nalisin.audio_to_midi_converter.utils.StaccatoAnalyzer.analyzeStaccato
import ru.spbu.apmath.nalisin.common_entities.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * @author s.nalisin
 */
object MidiNotesToNotesMapper {

    fun List<MidiNote>.toNotes(bpm: Int, timeSignature: TimeSignature): List<Note> {
        val notes = this
            .adjustNoteDuration()
            .divideNotes(bpm = bpm, timeSignature = timeSignature)
        println(this)
        println(notes)
        val notesWithStaccato = notes.analyzeStaccato()
        println(notesWithStaccato)
        return notesWithStaccato.map { noteWithStaccato ->
            val (rawNote, isStaccato) = noteWithStaccato
            val duration = durations[durations.keys.minByOrNull { abs(it - rawNote.duration) } ?: 0.0]
                ?: return@map null
            when (rawNote) {
                is MidiNote.Melodic -> {
                    val noteName = Note.Name.entries.firstOrNull { it.index == rawNote.getNoteIndex() }
                    val isNatural = noteName != null
                    Note.Melodic(
                        duration = duration,
                        octave = rawNote.getOctave(),
                        name = noteName ?: Note.Name.entries.first { it.index == rawNote.getNoteIndex() - 1 },
                        accidental = Note.Accidental.SHARP.takeUnless { isNatural } ?: Note.Accidental.NONE,
                        isStaccato = isStaccato,
                    )
                }

                is MidiNote.Rest -> Note.Rest(duration = duration)
            }
        }.filterNotNull()
    }

    private fun List<MidiNote>.adjustNoteDuration(deviation: Double = 0.0): List<MidiNote> {
        return this.map { note ->
            val wholes = note.duration.toInt()
            val rest = note.duration - wholes
            val closestRestDuration = durations.keys.minByOrNull { abs(it - (rest + deviation)) } ?: 0.0
            note.copy(duration = closestRestDuration + wholes)
        }
    }

    private fun calculateDurationDeviation(notes: List<MidiNote>): Double {
        val maxDeviation = 0.0625 // sixteenth

        var minError = Double.MAX_VALUE
        var optimalDeviation = 0.0
        val steps = 5

        for (i in -steps..steps) {
            val deviation = maxDeviation * i / steps
            var totalError = 0.0
            for (note in notes) {
                val wholes = note.duration.toInt()
                val rest = note.duration - wholes
                val adjustedDuration = durations.keys.minByOrNull { abs(it - (rest + deviation)) } ?: 0.0
                totalError += abs(adjustedDuration - rest)
            }

            if (totalError < minError) {
                minError = totalError
                optimalDeviation = deviation
            }
        }

        return optimalDeviation
    }

    private val durations: Map<Double, Note.Duration?> = mapOf(
        1.5 to Note.Duration.Whole(isDotted = true),
        1.0 to Note.Duration.Whole(isDotted = false),
        0.75 to Note.Duration.Half(isDotted = true),
        0.5 to Note.Duration.Half(isDotted = false),
        0.375 to Note.Duration.Quarter(isDotted = true),
        0.25 to Note.Duration.Quarter(isDotted = false),
        0.1875 to Note.Duration.Eighth(isDotted = true),
        0.125 to Note.Duration.Eighth(isDotted = false),
        0.09375 to Note.Duration.Sixteenth(isDotted = true),
        0.0625 to Note.Duration.Sixteenth(isDotted = false),
        0.046875 to Note.Duration.ThirtySecond(isDotted = true),
        0.03125 to Note.Duration.ThirtySecond(isDotted = false),
        0.0 to null
    )
}
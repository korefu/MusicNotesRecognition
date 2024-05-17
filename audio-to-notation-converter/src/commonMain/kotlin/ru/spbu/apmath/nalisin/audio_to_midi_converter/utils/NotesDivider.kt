package ru.spbu.apmath.nalisin.audio_to_midi_converter.utils

import ru.spbu.apmath.nalisin.common_entities.MidiNote
import ru.spbu.apmath.nalisin.common_entities.TimeSignature

/**
 * @author s.nalisin
 */
object NotesDivider {

    fun List<MidiNote>.divideNotes(
        bpm: Int,
        timeSignature: TimeSignature,
        minNoteDuration: Double = 0.0625,
    ): List<MidiNote> {
        val notes = this
        val barDuration = calculateBarDuration(timeSignature = timeSignature)
        var durationUntilTheEnd = barDuration
        return notes.flatMap { note ->
            if (note.duration <= durationUntilTheEnd + minNoteDuration) {
                durationUntilTheEnd -= note.duration
                if (durationUntilTheEnd < minNoteDuration) durationUntilTheEnd = barDuration
                listOf(note)
            } else {
                val dividedDurations = mutableListOf<Double>()
                var remainedNoteDuration = note.duration
                do {
                    if (remainedNoteDuration < durationUntilTheEnd) {
                        dividedDurations.add(remainedNoteDuration)
                        remainedNoteDuration -= note.duration
                        durationUntilTheEnd -= note.duration
                    } else {
                        dividedDurations.add(durationUntilTheEnd)
                        remainedNoteDuration -= durationUntilTheEnd
                        durationUntilTheEnd = barDuration
                    }
                    if (durationUntilTheEnd < minNoteDuration) durationUntilTheEnd = barDuration
                } while (remainedNoteDuration > minNoteDuration)
                dividedDurations.map { noteDuration ->
                    when (note) {
                        is MidiNote.Melodic -> MidiNote.Melodic(value = note.value, duration = noteDuration)
                        is MidiNote.Rest -> MidiNote.Rest(duration = noteDuration)
                    }
                }
            }
        }
    }

    private fun calculateBarDuration(timeSignature: TimeSignature): Double {
        return timeSignature.beatsPerBar.toDouble() / timeSignature.beatType
    }
}
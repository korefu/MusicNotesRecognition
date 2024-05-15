package ru.spbu.apmath.nalisin.audio_to_midi_converter.mappers

import ru.spbu.apmath.nalisin.common_entities.NOTE_NAMES_SHARP
import ru.spbu.apmath.nalisin.common_entities.MidiNote
import kotlin.math.abs
import kotlin.math.floor

expect object MidiNotesToMidiDataMapper {

    fun notesToMidi(notes: List<MidiNote>, bpm: Int): ByteArray
}

fun MidiNote.getPattern(): String = when (this) {
    is MidiNote.Melodic -> NOTE_NAMES_SHARP[value % 12] + value / 12 + translateDuration(duration)
    is MidiNote.Rest -> "R" + translateDuration(duration)
}

private fun translateDuration(duration: Double): String {
    var decimalDuration = duration
    val buddy = StringBuilder()
    if (decimalDuration >= 1.0) {
        val numWholeDurations = floor(decimalDuration).toInt()
        buddy.append("w")
        if (numWholeDurations > 1) {
            buddy.append(numWholeDurations)
        }
        decimalDuration -= numWholeDurations.toDouble()
    }
    val durations = mapOf(
        0.75 to "h.",
        0.5 to "h",
        0.375 to "q.",
        0.25 to "q",
        0.1875 to "i.",
        0.125 to "i",
        0.09375 to "s.",
        0.0625 to "s",
        0.046875 to "t.",
        0.03125 to "t",
        0.0234375 to "x.",
        0.015625 to "x",
        0.01171875 to "o.",
        0.0078125 to "o",
    )

    val closestDuration = durations.keys.minByOrNull { abs(it - decimalDuration) } ?: 0.25
    return buddy.append(durations[closestDuration] ?: "q").toString() // quarter note as default
}
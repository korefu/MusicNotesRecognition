package ru.spbu.apmath.nalisin.audio_to_midi_converter.mappers

import ru.spbu.apmath.nalisin.common_entities.MidiNote

actual object MidiNotesToMidiDataMapper {
    actual fun notesToMidi(
        notes: List<MidiNote>,
        bpm: Int,
    ): ByteArray {
        val pattern = NotesToPatternMapper.translateNotesToPattern(notes = notes, bpm = bpm)
        return PatternToMidiMapper.patternToMidi(pattern = pattern)
    }

}
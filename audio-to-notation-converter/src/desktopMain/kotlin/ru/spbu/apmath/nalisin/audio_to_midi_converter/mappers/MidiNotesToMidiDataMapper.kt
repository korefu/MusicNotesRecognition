package ru.spbu.apmath.nalisin.audio_to_midi_converter.mappers

import ru.spbu.apmath.nalisin.audio_to_midi_converter.mappers.NotesToPatternMapper.translateNotesToPattern
import ru.spbu.apmath.nalisin.audio_to_midi_converter.mappers.PatternToMidiMapper.patternToMidi
import ru.spbu.apmath.nalisin.common_entities.MidiNote

actual object MidiNotesToMidiDataMapper {
    actual fun notesToMidi(notes: List<MidiNote>, bpm: Int): ByteArray {
        val pattern = translateNotesToPattern(notes = notes, bpm = bpm)
        return patternToMidi(pattern = pattern)
    }
}
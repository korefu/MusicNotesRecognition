package ru.spbu.apmath.nalisin.audio_to_midi_converter.mappers

import org.jfugue.pattern.Pattern
import ru.spbu.apmath.nalisin.common_entities.MidiNote

object NotesToPatternMapper {

    fun translateNotesToPattern(notes: List<MidiNote>, bpm: Int): Pattern {
        val pattern = Pattern()
        pattern.setTempo(bpm)
        var isFirstNote = true

        notes.forEach { note ->
            // Проверяем, является ли нота паузой
            if (note is MidiNote.Rest && isFirstNote) {
                // Первую паузу игнорируем
                isFirstNote = false
            } else {
                pattern.add(note.getPattern())
                isFirstNote = false
            }
        }
        return pattern
    }
}
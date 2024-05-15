package ru.spbu.apmath.nalisin.audio_to_midi_converter

import ru.spbu.apmath.nalisin.common_entities.MidiNote
import ru.spbu.apmath.nalisin.common_entities.TimeFrequency
import ru.spbu.apmath.nalisin.common_entities.UniversalAudioFormat

/**
 * @author s.nalisin
 */
interface MidiNotesComposer {

    fun composeMidiNotes(
        audioFilePath: String,
        settings: Settings,
    ): List<MidiNote>

    fun composeMidiNotes(
        audioData: ByteArray,
        audioFormat: UniversalAudioFormat,
        settings: Settings,
    ): List<MidiNote>

    fun composeMidiNotes(
        frequencies: List<TimeFrequency>,
        settings: Settings,
    ): List<MidiNote>
}
package ru.spbu.apmath.nalisin.audio_to_midi_converter

import ru.spbu.apmath.nalisin.common_entities.MidiNote

/**
 * @author s.nalisin
 */
interface MidiConverter {

    fun convertToMidi(
        midiNotes: List<MidiNote>,
        settings: Settings,
    ): ByteArray
}
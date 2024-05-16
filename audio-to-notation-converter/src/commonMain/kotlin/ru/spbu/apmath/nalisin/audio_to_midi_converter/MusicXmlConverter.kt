package ru.spbu.apmath.nalisin.audio_to_midi_converter

import ru.spbu.apmath.nalisin.common_entities.MidiNote


interface MusicXmlConverter {

    fun convertToMusicXml(
        midiNotes: List<MidiNote>,
        settings: Settings,
    ): String
}

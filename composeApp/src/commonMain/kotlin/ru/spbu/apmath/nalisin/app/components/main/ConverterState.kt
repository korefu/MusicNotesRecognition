package ru.spbu.apmath.nalisin.app.components.main

import ru.spbu.apmath.nalisin.common_entities.MidiNote

sealed interface ConverterState {
    object Initial : ConverterState
    object Processing : ConverterState
    sealed class MidiNotesReceived(val midiNotes: List<MidiNote>): ConverterState {
        class Init(midiNotes: List<MidiNote>): MidiNotesReceived(midiNotes)
        class MusicXmlReceived(midiNotes: List<MidiNote>, val musicXml: String): MidiNotesReceived(midiNotes)
        class MidiReceived(midiNotes: List<MidiNote>, val midi: ByteArray): MidiNotesReceived(midiNotes)
    }
}

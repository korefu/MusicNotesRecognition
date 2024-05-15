package ru.spbu.apmath.nalisin.audio_to_midi_converter

import me.tatarka.inject.annotations.Inject
import ru.spbu.apmath.nalisin.audio_to_midi_converter.mappers.MidiNotesToMidiDataMapper
import ru.spbu.apmath.nalisin.common_entities.MidiNote

/**
 * @author s.nalisin
 */
@Inject
class MidiConverterImpl : MidiConverter {

    override fun convertToMidi(midiNotes: List<MidiNote>, settings: Settings): ByteArray {
        return MidiNotesToMidiDataMapper.notesToMidi(notes = midiNotes, bpm = settings.bpm)
    }
}
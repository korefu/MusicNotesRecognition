package ru.spbu.apmath.nalisin.audio_to_midi_converter

import com.example.musicxml_writer.MusicXmlCreator
import me.tatarka.inject.annotations.Inject
import ru.spbu.apmath.nalisin.audio_to_midi_converter.mappers.MidiNotesToNotesMapper.toNotes
import ru.spbu.apmath.nalisin.audio_to_midi_converter.mappers.NotesToMeasuresMapper.toMeasures
import ru.spbu.apmath.nalisin.common_entities.MidiNote

@Inject
class MusicXmlConverterImpl(
    private val musicXmlCreator: MusicXmlCreator,
) : MusicXmlConverter {

    override fun convertToMusicXml(midiNotes: List<MidiNote>, settings: Settings): String {
        val notes = midiNotes.toNotes(bpm = settings.bpm, timeSignature = settings.timeSignature)
        return musicXmlCreator.getMusicXml(
            bpm = settings.bpm,
            measures = notes.toMeasures(timeSignature = settings.timeSignature),
            beats = settings.timeSignature.beatsPerBar,
            beatType = settings.timeSignature.beatType,
        )
    }
}
package ru.spbu.apmath.nalisin.audio_to_midi_converter.mappers

import org.jfugue.midi.MidiFileManager
import org.jfugue.pattern.Pattern
import java.io.ByteArrayOutputStream

object PatternToMidiMapper {

    fun patternToMidi(pattern: Pattern): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        try {
            MidiFileManager.savePatternToMidi(pattern, byteArrayOutputStream)
            println("MIDI file saved")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return byteArrayOutputStream.toByteArray()
    }
}
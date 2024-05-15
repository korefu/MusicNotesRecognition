package ru.spbu.apmath.nalisin.audio_to_midi_converter.utils

import jp.kshoji.javax.sound.midi.MidiSystem
import jp.kshoji.javax.sound.midi.Sequence
import org.jfugue.Pattern
import org.jfugue.Player
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


object MidiFileManager {
    @Throws(IOException::class)
    fun save(sequence: Sequence?, out: OutputStream?) {
        val writers = MidiSystem.getMidiFileTypes(sequence)
        if (writers.isEmpty()) return
        MidiSystem.write(sequence, writers[0], out)
    }

    @Throws(IOException::class)
    fun savePatternToMidi(pattern: Pattern?, out: OutputStream?) {
        save(Player().getSequence(pattern), out)
    }

    /**
     * Convenience method to make it easier to save a file
     */
    @Throws(IOException::class)
    fun savePatternToMidi(pattern: Pattern?, file: File?) {
        savePatternToMidi(pattern, FileOutputStream(file))
    }
}

package ru.spbu.apmath.nalisin.common_entities

import kotlin.math.ceil

sealed interface MidiNote {

    val duration: Double

    fun copy(duration: Double): MidiNote

    class Melodic(val value: Int, override val duration: Double) : MidiNote {
        override fun copy(duration: Double): MidiNote = Melodic(value = this.value, duration = duration)

        override fun toString(): String {
            return "${NOTE_NAMES_SHARP[getNoteIndex()]}${getOctave()}|$duration}"
        }
    }

    class Rest(override val duration: Double) : MidiNote {
        override fun copy(duration: Double): MidiNote = Rest(duration = duration)

        override fun toString(): String {
            return "R|$duration"
        }
    }
}

fun MidiNote.Melodic.getOctave(): Int = ceil((value - 23) / 12.0).toInt()

fun MidiNote.Melodic.getNoteIndex(): Int = value % 12
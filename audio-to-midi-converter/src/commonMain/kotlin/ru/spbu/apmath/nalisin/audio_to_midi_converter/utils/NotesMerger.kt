package ru.spbu.apmath.nalisin.audio_to_midi_converter.utils

import ru.spbu.apmath.nalisin.common_entities.MidiNote

object NotesMerger {

    // объединяет одинаковые идущие подряд ноты
    fun List<MidiNote>.mergeNotes(bpm: Int): List<MidiNote> {
        val mergedNotes = mutableListOf<MidiNote>()
        var newNote: MidiNote? = null

        this.forEach { nextNote ->
            val acc = newNote
            if (acc == null) {
                newNote = nextNote
            } else {
                when {
                    nextNote is MidiNote.Rest && acc is MidiNote.Rest -> {
                        newNote = MidiNote.Rest(duration = acc.duration + nextNote.duration)
                    }

                    nextNote is MidiNote.Melodic && acc is MidiNote.Melodic && nextNote.value == acc.value -> {
                        newNote = MidiNote.Melodic(
                            value = acc.value,
                            duration = acc.duration + nextNote.duration,
                        )
                    }

                    else -> {
                        mergedNotes.add(acc)
                        newNote = nextNote
                    }
                }
            }
        }
        // Добавляем последнюю ноту
        newNote?.let { mergedNotes.add(it) }

        return mergedNotes
    }
}
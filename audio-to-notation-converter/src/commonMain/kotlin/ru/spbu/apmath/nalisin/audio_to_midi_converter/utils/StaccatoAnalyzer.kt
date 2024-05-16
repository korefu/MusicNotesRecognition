package ru.spbu.apmath.nalisin.audio_to_midi_converter.utils

import ru.spbu.apmath.nalisin.common_entities.MidiNote
import ru.spbu.apmath.nalisin.common_entities.MidiNote.Melodic
import ru.spbu.apmath.nalisin.common_entities.MidiNote.Rest

/**
 * @author s.nalisin
 */
object StaccatoAnalyzer {

    // на вход подаются ноты, которые были предварительно объединены и снова разделены по границам такта
    fun List<MidiNote>.analyzeStaccato(): List<Pair<MidiNote, Boolean>> {
        val notes = this
        val quarterNoteDuration = 0.25
        val eighthNoteDuration = 0.125
        val staccatoThreshold = 1.5
        val nonLegatoThreshold = 2.0
        val analyzedNotes = mutableListOf<Pair<MidiNote, Boolean>>()
        var noteAсс: Melodic? = null
        var index = 0
        while (index < notes.size) {
            val currentNote = noteAсс ?: notes[index]
            val nextNote = notes.getOrNull(index + 1)
            when {
                nextNote == null -> {
                    analyzedNotes.add(currentNote to false)
                    index++
                }

                currentNote is Rest && nextNote is Melodic -> {
                    // это должна быть пауза в начале такта, иначе алгоритм некорректный
                    // если длительность меньше восьмой и следующая нота в два раза длиннее, то присоединяем паузу к ноте
                    if (currentNote.duration < eighthNoteDuration && currentNote.duration * 2 <= nextNote.duration) {
                        noteAсс = Melodic(value = nextNote.value, duration = currentNote.duration + nextNote.duration)
                    } else {
                        analyzedNotes.add(currentNote to false)
                    }
                    index++
                }

                currentNote is Rest && nextNote is Rest -> {
                    analyzedNotes.add(currentNote to false)
                    index++
                }

                currentNote is Melodic && nextNote is Melodic -> {
                    analyzedNotes.add(currentNote to false)
                    noteAсс = null
                    index++
                }

                currentNote is Melodic && nextNote is Rest -> {
                    val ratio = currentNote.duration / nextNote.duration
                    when {
                        ratio < staccatoThreshold -> {
                            analyzedNotes.add(
                                Melodic(
                                    value = currentNote.value,
                                    duration = currentNote.duration + nextNote.duration
                                ) to true
                            )
                        }

                        ratio in (staccatoThreshold..nonLegatoThreshold) -> {
                            analyzedNotes.add(currentNote to false)
                            analyzedNotes.add(nextNote to false)
                        }

                        ratio > nonLegatoThreshold -> {
                            analyzedNotes.add(
                                Melodic(
                                    value = currentNote.value,
                                    duration = currentNote.duration + nextNote.duration
                                ) to false
                            )
                        }
                    }
                    noteAсс = null
                    index += 2
                }
            }
        }
        return analyzedNotes
    }
}
package ru.spbu.apmath.nalisin.audio_to_midi_converter.utils

import ru.spbu.apmath.nalisin.common_entities.MidiNote
import ru.spbu.apmath.nalisin.common_entities.MidiNote.Melodic
import ru.spbu.apmath.nalisin.common_entities.MidiNote.Rest

object NotesSmoother {

    // заменить каждую ноту на самую часто встречающуюся среди ее соседей в интервале
    fun List<MidiNote>.smoothNotes(interval: Int): List<MidiNote> {
        if (this.size <= interval * 2) {
            // Недостаточно данных для анализа, возвращаем исходный список
            return this
        }

        // Сюда будем записывать аппроксимированный список нот.
        val approximatedNotes = mutableListOf<MidiNote>()

        // Проходимся по всем нотам списка
        this.forEachIndexed { index, currentNote ->
            when (currentNote) {
                is Melodic -> {
                    val start = maxOf(0, index - interval)
                    val end = minOf(this.size - 1, index + interval)
                    val notesFrequency = mutableMapOf<Melodic, Int>()

                    // Считаем частоты нот без учета тишины
                    for (j in start..end) {
                        val currentNote = this[j]
                        // Проверяем, не является ли текущая нота тишиной
                        if (currentNote is Melodic) {
                            notesFrequency[currentNote] = (notesFrequency[currentNote] ?: 0) + 1
                        }
                    }

                    // Находим самую частую ноту, исключая тишину
                    val mostFrequentNote = notesFrequency.maxByOrNull { it.value }?.key
                        ?: currentNote // В случае полной тишины в интервале сохраняем текущую ноту

                    // Добавление ноты в результирующий список с сохранением длительности
                    approximatedNotes.add(
                        Melodic(
                            mostFrequentNote.value,
                            currentNote.duration,
                        )
                    )
                }

                is Rest -> {
                    approximatedNotes.add(currentNote)
                }
            }
        }

        return approximatedNotes
    }

    // cоединить ноты в интервале в одну самую часто всречающуюся
    fun List<MidiNote>.mergeNotesInGroups(intervalLength: Int): List<MidiNote> {
        val mergedNotes = mutableListOf<MidiNote>()
        var currentIntervalDuration = 0.0
        var noteCounter = 0
        val currentIntervalNotes = mutableListOf<MidiNote>()

        this.forEach { note ->
            currentIntervalNotes.add(note)
            currentIntervalDuration += note.duration
            noteCounter++

            if (noteCounter == intervalLength) {
                val mostFrequentNote = currentIntervalNotes.filterIsInstance<Melodic>()
                    .groupingBy { it.value }
                    .eachCount()
                    .maxByOrNull { it.value }?.key

                mostFrequentNote?.let {
                    mergedNotes.add(Melodic(it, currentIntervalDuration))
                } ?: run {
                    // Если в интервале нет ни одной звучащей ноты, добавляем паузу
                    mergedNotes.add(Rest(currentIntervalDuration))
                }

                // Сброс счетчика нот и длительности для следующего интервала
                noteCounter = 0
                currentIntervalDuration = 0.0
                currentIntervalNotes.clear()
            }
        }

        // Если в конце остались ноты, которые не образуют полный интервал, добавляем их как есть
        if (currentIntervalNotes.isNotEmpty()) {
            mergedNotes.addAll(currentIntervalNotes)
        }

        return mergedNotes
    }

    // присоединять слишком корооткие ноты к соседним с идеей того, что это нежелательное отклонение
    fun List<MidiNote>.postProcessNotes(
        minNoteDurationThreshold: Double,
        minRestDurationThreshold: Double = minNoteDurationThreshold,
    ): List<MidiNote> {
        val notes = this.toMutableList()
        println(notes)

        var index = 0
        while (index < notes.size) {
            val currentNote = notes[index]
            val previousNote = notes.getOrNull(index - 1)
            val nextNote = notes.getOrNull(index + 1)

            when {
                (currentNote is Melodic && currentNote.duration >= minNoteDurationThreshold)
                        || (currentNote is Rest && currentNote.duration >= minRestDurationThreshold) -> {
                    index++
                }

                nextNote == null && previousNote != null -> {
                    notes[index - 1] = previousNote.copy(duration = previousNote.duration + currentNote.duration)
                    notes.removeAt(index)
                }

                nextNote != null && previousNote == null -> {
                    notes[index + 1] = nextNote.copy(duration = nextNote.duration + currentNote.duration)
                    notes.removeAt(index)
                }

                nextNote == null && previousNote == null -> {
                    index++
                }

                nextNote != null && previousNote != null -> {
                    val pitchDifference = if (nextNote is Melodic && previousNote is Melodic) {
                        nextNote.value - previousNote.value
                    } else if (nextNote is Rest && previousNote is Rest) 0 else null
                    val durationDifference = nextNote.duration - previousNote.duration
                    when {
                        pitchDifference == 0 -> {
                            notes[index - 1] =
                                previousNote.copy(duration = previousNote.duration + currentNote.duration + nextNote.duration)
                            notes.removeAt(index)
                            notes.removeAt(index)
                        }

                        durationDifference > currentNote.duration * 2 -> {
                            notes[index - 1] =
                                previousNote.copy(duration = previousNote.duration + currentNote.duration)
                            notes.removeAt(index)
                        }

                        durationDifference < -(currentNote.duration * 2) -> {
                            notes[index + 1] = nextNote.copy(duration = nextNote.duration + currentNote.duration)
                            notes.removeAt(index)
                        }

                        else -> {
                            val dividedDuration = (previousNote.duration + currentNote.duration + nextNote.duration) / 2
                            notes[index - 1] = previousNote.copy(duration = dividedDuration)
                            notes[index + 1] = nextNote.copy(duration = dividedDuration)
                            notes.removeAt(index)
                        }
                    }

                    val newCurrent = notes.getOrNull(index)
                    val newNext = notes.getOrNull(index + 1)
                    if (newCurrent is Melodic && newNext is Melodic && newCurrent.value == newNext.value) {
                        notes[index] = newCurrent.copy(duration = newCurrent.duration + newNext.duration)
                        notes.removeAt(index + 1)
                    }
                }

                else -> {
                    index++
                }
            }
        }
        return notes
    }
}
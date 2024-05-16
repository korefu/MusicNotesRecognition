package ru.spbu.apmath.nalisin.audio_to_midi_converter.utils

import ru.spbu.apmath.nalisin.common_entities.MidiNote
import kotlin.math.abs

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
                is MidiNote.Melodic -> {
                    val start = maxOf(0, index - interval)
                    val end = minOf(this.size - 1, index + interval)
                    val notesFrequency = mutableMapOf<MidiNote.Melodic, Int>()

                    // Считаем частоты нот без учета тишины
                    for (j in start..end) {
                        val currentNote = this[j]
                        // Проверяем, не является ли текущая нота тишиной
                        if (currentNote is MidiNote.Melodic) {
                            notesFrequency[currentNote] = (notesFrequency[currentNote] ?: 0) + 1
                        }
                    }

                    // Находим самую частую ноту, исключая тишину
                    val mostFrequentNote = notesFrequency.maxByOrNull { it.value }?.key
                        ?: currentNote // В случае полной тишины в интервале сохраняем текущую ноту

                    // Добавление ноты в результирующий список с сохранением длительности
                    approximatedNotes.add(
                        MidiNote.Melodic(
                            mostFrequentNote.value,
                            currentNote.duration,
                        )
                    )
                }

                is MidiNote.Rest -> {
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
                val mostFrequentNote = currentIntervalNotes.filterIsInstance<MidiNote.Melodic>()
                    .groupingBy { it.value }
                    .eachCount()
                    .maxByOrNull { it.value }?.key

                mostFrequentNote?.let {
                    mergedNotes.add(MidiNote.Melodic(it, currentIntervalDuration))
                } ?: run {
                    // Если в интервале нет ни одной звучащей ноты, добавляем паузу
                    mergedNotes.add(MidiNote.Rest(currentIntervalDuration))
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
    fun List<MidiNote>.postProcessNotes(minDurationThreshold: Double): List<MidiNote> {
        val notes = this
        println(notes)
        val processedNotes = mutableListOf<MidiNote>()

        var index = 0
        while (index < notes.size) {
            val currentNote = notes[index]
            if (currentNote.duration >= minDurationThreshold) {
                processedNotes.add(currentNote)
                index++
            } else {
                if (currentNote !is MidiNote.Melodic) {
                    processedNotes.add(currentNote)
                    index++
                } else {
                    val previousNote = processedNotes.lastOrNull()
                    val nextNote = if (index < notes.size - 1) notes[index + 1] else null
                    when {
                        nextNote is MidiNote.Melodic && previousNote !is MidiNote.Melodic -> {
                            processedNotes.add(
                                MidiNote.Melodic(
                                    value = nextNote.value,
                                    duration = currentNote.duration + nextNote.duration
                                )
                            )
                            index += 2
                        }

                        nextNote !is MidiNote.Melodic && previousNote is MidiNote.Melodic -> {
                            processedNotes[processedNotes.size - 1] = MidiNote.Melodic(
                                value = previousNote.value,
                                duration = currentNote.duration + previousNote.duration
                            )
                            index++
                        }

                        nextNote is MidiNote.Melodic && previousNote is MidiNote.Melodic -> {
                            val noteDifference =
                                (abs(nextNote.value - currentNote.value) -
                                        abs(previousNote.value - currentNote.value)).toDouble().takeIf { it != 0.0 }
                                    ?: (previousNote.duration - nextNote.duration)
                            when {
                                noteDifference >= 0 -> {
                                    processedNotes[processedNotes.size - 1] = MidiNote.Melodic(
                                        value = previousNote.value,
                                        duration = currentNote.duration + previousNote.duration
                                    )
                                    index++
                                }
                                else -> {
                                    processedNotes.add(
                                        MidiNote.Melodic(
                                            value = nextNote.value,
                                            duration = currentNote.duration + nextNote.duration
                                        )
                                    )
                                    index += 2
                                }
                            }
                        }

                        else -> {
                            processedNotes.add(currentNote)
                            index++
                        }
                    }
                }
            }
        }

        // Возвращаем список обработанных нот
        return processedNotes
    }
}
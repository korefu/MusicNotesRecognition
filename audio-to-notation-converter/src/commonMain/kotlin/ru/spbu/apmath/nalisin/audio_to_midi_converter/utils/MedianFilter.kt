package ru.spbu.apmath.nalisin.audio_to_midi_converter.utils

import ru.spbu.apmath.nalisin.common_entities.MidiNote


object MedianFilter {

    fun List<MidiNote>.applyMedianFilter(windowSize: Int): List<MidiNote> {
        val frequencies = this
        // Если список слишком мал для применения фильтра
        if (frequencies.size < windowSize || windowSize < 3) return frequencies

        val newFrequencies = mutableListOf<MidiNote>()

        for (i in frequencies.indices) {
            val windowFrequencies = frequencies.subList(
                maxOf(0, i - windowSize / 2),
                minOf(frequencies.size, i + windowSize / 2 + 1)
            ).filterIsInstance<MidiNote.Melodic>()

            if (windowFrequencies.isNotEmpty()) {
                val medianValue = windowFrequencies.sortedBy { it.value }[windowFrequencies.size / 2]
                newFrequencies.add(medianValue)
            } else {
                newFrequencies.add(frequencies[i])
            }
        }

        return newFrequencies
    }
}
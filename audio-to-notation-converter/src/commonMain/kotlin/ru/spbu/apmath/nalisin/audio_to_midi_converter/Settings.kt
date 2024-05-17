package ru.spbu.apmath.nalisin.audio_to_midi_converter

import ru.spbu.apmath.nalisin.common_entities.TimeSignature

data class Settings(
    // todo определять bpm
    val bpm: Int = 120,
    // todo какое оптимальное окно?
    val fragmentDurationInMillis: Long = 20L,
    val medianFilterWindowSize: Int = 3,
    val minDurationThreshold: Double = 0.053,
    val timeSignature: TimeSignature = TimeSignature(beatsPerBar = 4, beatType = 4)
)

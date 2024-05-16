package ru.spbu.apmath.nalisin.frequency_recognition_api

import ru.spbu.apmath.nalisin.common_entities.MusicFile

interface FrequencyRecognizer {
    fun recognizeFrequency(audioFilePath: String): Double?
    fun recognizeFrequency(musicFile: MusicFile): Double?
}
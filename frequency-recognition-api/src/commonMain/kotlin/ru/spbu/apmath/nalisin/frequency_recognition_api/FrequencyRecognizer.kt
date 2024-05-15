package ru.spbu.apmath.nalisin.frequency_recognition_api

import ru.spbu.apmath.nalisin.common_entities.UniversalAudioFormat

interface FrequencyRecognizer {
    fun recognizeFrequency(audioFilePath: String): Double?
    fun recognizeFrequency(audioData: ByteArray, audioFormat: UniversalAudioFormat): Double?
}
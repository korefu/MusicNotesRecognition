package ru.spbu.apmath.nalisin.loudness_analyzer

import ru.spbu.apmath.nalisin.common_entities.MusicFile

interface LoudnessAnalyzer {

    fun getAverageLoudness(audioFilePath: String): Double

    fun getAverageLoudness(musicFile: MusicFile): Double
}
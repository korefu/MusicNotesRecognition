package ru.spbu.apmath.nalisin.audio_splitter

import ru.spbu.apmath.nalisin.common_entities.UniversalAudioFormat

interface AudioSplitter {
    fun splitAudio(audioData: ByteArray, audioFormat: UniversalAudioFormat, durationInMillis: Long): List<ByteArray>
    fun splitAudio(filePath: String, durationInMillis: Long): List<ByteArray>
}

package ru.spbu.apmath.nalisin.audio_splitter

import ru.spbu.apmath.nalisin.common_entities.MusicFile

interface AudioSplitter {
    fun splitAudio(musicFile: MusicFile, durationInMillis: Long): List<ByteArray>
    fun splitAudio(filePath: String, durationInMillis: Long): List<ByteArray>
}

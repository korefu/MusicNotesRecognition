package ru.spbu.apmath.nalisin.app.components.main.recorder

import ru.spbu.apmath.nalisin.common_entities.MusicFile

interface VoiceRecorder {

    suspend fun getAudio(): MusicFile

    suspend fun start(): Boolean

    fun pause()

    fun stop()

    suspend fun save(outputPath: String = "recording.wav"): MusicFile
}

expect class VoiceRecorderImpl : VoiceRecorder
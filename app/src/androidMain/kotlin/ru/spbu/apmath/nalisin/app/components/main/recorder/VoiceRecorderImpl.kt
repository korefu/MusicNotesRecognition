package ru.spbu.apmath.nalisin.app.components.main.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import ru.spbu.apmath.nalisin.app.runSuspendCatching
import ru.spbu.apmath.nalisin.common_entities.MusicFile
import ru.spbu.apmath.nalisin.common_utils.toUniversalAudioFormat
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import kotlin.coroutines.CoroutineContext

@Inject
actual class VoiceRecorderImpl(
    private val ioContext: CoroutineContext = Dispatchers.IO,
) : VoiceRecorder {

    private val encoding = AudioFormat.ENCODING_PCM_16BIT
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val sampleRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, encoding) * 2
    private val format = AudioFormat.Builder()
        .setSampleRate(sampleRate)
        .setChannelMask(channelConfig)
        .setEncoding(encoding)
        .build()
    private var audioRecord: AudioRecord? = null
    private var recordingBuffer = ByteArrayOutputStream()
    private var recordingJob: Job? = null

    override suspend fun getAudio(): MusicFile {
        pause()
        val audioData = recordingBuffer.toByteArray()
        reset()

        return MusicFile(
            audioData = audioData,
            format = format.toUniversalAudioFormat(),
        )
    }

    override suspend fun start() = coroutineScope {
        if (audioRecord == null) {
            audioRecord = runSuspendCatching {
                AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, encoding, bufferSize)
            }.onFailure { Log.e("MNR", "Don't have microphone permission") }.getOrElse { return@coroutineScope false }
        }
        recordingJob = launch { captureAudio() }
        true
    }

    private suspend fun captureAudio() = withContext(ioContext) {
        audioRecord?.startRecording()
        val audioData = ByteArray(bufferSize)
        while (recordingJob?.isActive == true) {
            val readSize = audioRecord?.read(audioData, 0, audioData.size) ?: 0
            val bytes = ByteArray(readSize).apply {
                System.arraycopy(audioData, 0, this, 0, readSize)
            }
            recordingBuffer.write(bytes)
        }
    }

    override fun pause() {
        recordingJob?.cancel()
        audioRecord?.stop()
    }

    override fun stop() {
        pause()
        reset()
    }

    private fun reset() {
        audioRecord?.release()
        audioRecord = null
        recordingBuffer.reset()
    }

    override suspend fun save(outputPath: String): MusicFile = withContext(ioContext) {
        val musicFile = getAudio()
        val wavData = musicFile.audioData
        FileOutputStream(outputPath).use { it.write(wavData) }
        musicFile
    }

}
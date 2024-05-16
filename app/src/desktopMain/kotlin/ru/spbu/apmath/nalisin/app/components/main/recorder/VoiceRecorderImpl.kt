package ru.spbu.apmath.nalisin.app.components.main.recorder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import ru.spbu.apmath.nalisin.common_entities.MusicFile
import ru.spbu.apmath.nalisin.common_utils.toUniversalAudioFormat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.coroutines.CoroutineContext

@Inject
actual class VoiceRecorderImpl(
    private val ioContext: CoroutineContext = Dispatchers.IO,
) : VoiceRecorder {

    private var captureJob: Job? = null
    private val format = AudioFormat(41000f, 16, 1, true, false)
    private val targetDataLine = AudioSystem.getTargetDataLine(format)
    private val outputStream = ByteArrayOutputStream()

    override suspend fun start(): Boolean = coroutineScope {
        if (captureJob == null) {
            targetDataLine.open(format)
        }
        captureJob = launch { captureAudio() }
        true
    }

    override fun pause() {
        captureJob?.cancel()
        targetDataLine.stop()
    }

    override fun stop() {
        pause()
        synchronized(outputStream) {
            outputStream.reset()
        }
    }

    override suspend fun getAudio(): MusicFile {
        pause()
        return synchronized(outputStream) {
            MusicFile(
                audioData = outputStream.toByteArray(),
                format = format.toUniversalAudioFormat(),
            )
        }
    }

    override suspend fun save(outputPath: String): MusicFile = withContext(ioContext) {
        val audio = getAudio()
        val audioBytes = audio.audioData
        val inputStream = AudioInputStream(
            audioBytes.inputStream(),
            format,
            (audioBytes.size / format.frameSize).toLong()
        )
        FileOutputStream(File(outputPath), false).use { fileOutputStream ->
            AudioSystem.write(inputStream, AudioFileFormat.Type.WAVE, fileOutputStream)
        }
        return@withContext audio
    }

    private suspend fun captureAudio() = withContext(ioContext) {
        targetDataLine.start()
        val buffer = ByteArray(2048)
        while (captureJob?.isActive == true) {
            val bytesRead = targetDataLine.read(buffer, 0, buffer.size)
            if (bytesRead > 0) outputStream.write(buffer, 0, bytesRead)
        }
    }
}
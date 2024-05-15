package ru.spbu.apmath.nalisin.common_utils

import android.media.AudioFormat
import android.media.MediaExtractor
import android.media.MediaFormat
import me.tatarka.inject.annotations.Inject
import ru.spbu.apmath.nalisin.common_entities.UniversalAudioFormat

@Inject
actual class GetAudioFormatUseCaseImpl : GetAudioFormatUseCase {

    actual override operator fun invoke(filePath: String): UniversalAudioFormat {
        val mediaExtractor = MediaExtractor()
        var audioFormat: AudioFormat? = null

        try {
            mediaExtractor.setDataSource(filePath)

            // Найдем дорожку аудио в файле
            val numTracks = mediaExtractor.trackCount
            for (i in 0 until numTracks) {
                val format = mediaExtractor.getTrackFormat(i)
                val mimeType = format.getString(MediaFormat.KEY_MIME)

                if (mimeType?.startsWith("audio/") == true) {
                    // Это аудио дорожка, извлекаем информацию
                    val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                    val channelMask = format.getInteger(MediaFormat.KEY_CHANNEL_MASK)
                    val encoding = format.getInteger(MediaFormat.KEY_PCM_ENCODING)

                    audioFormat = AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(channelMask)
                        .setEncoding(encoding)
                        .build()

                    // Нашли первую аудио дорожку, прерываем цикл
                    break
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaExtractor.release()
        }

        return audioFormat!!.toUniversalAudioFormat()
    }
}
package ru.spbu.apmath.nalisin.audio_splitter

import me.tatarka.inject.annotations.Inject
import ru.spbu.apmath.nalisin.common_entities.MusicFile
import ru.spbu.apmath.nalisin.common_utils.GetAudioFormatUseCase
import java.io.File

@Inject
class AudioSplitterImpl(
    private val getAudioFormatUseCase: GetAudioFormatUseCase,
) : AudioSplitter {

    override fun splitAudio(
        musicFile: MusicFile,
        durationInMillis: Long,
    ): List<ByteArray> {
        // Расчет количества байтов в миллисекунду
        val bytesPerMillisecond = with(musicFile.format) {
            (sampleRate * sampleSizeInBits * channels / 8 / 1000).toLong()
        }

        // Расчет общего количества байтов в одном интервале
        val intervalByteSize = (durationInMillis * bytesPerMillisecond).toInt()

        // Разбиение аудио файла на интервалы
        val audioIntervals = mutableListOf<ByteArray>()

        var start = 0
        // Оставшееся количество байт для обработки
        var remainingBytes = musicFile.audioData.size
        while (remainingBytes > 0) {
            // Размер текущего фрагмента зависит от оставшихся байт
            val end = start + intervalByteSize.coerceAtMost(remainingBytes)
            audioIntervals.add(musicFile.audioData.copyOfRange(start, end))
            // Обновление начального индекса и оставшегося количества байт
            start = end
            remainingBytes -= intervalByteSize
        }

        return audioIntervals
    }

    override fun splitAudio(
        filePath: String,
        durationInMillis: Long,
    ): List<ByteArray> {
        return splitAudio(
            musicFile = MusicFile(
                audioData = File(filePath).readBytes(),
                format = getAudioFormatUseCase(filePath)
            ),
            durationInMillis = durationInMillis,
        )
    }
}

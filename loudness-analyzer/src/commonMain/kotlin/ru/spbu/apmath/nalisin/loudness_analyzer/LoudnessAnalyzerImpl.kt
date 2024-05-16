package ru.spbu.apmath.nalisin.loudness_analyzer

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.UniversalAudioInputStream
import me.tatarka.inject.annotations.Inject
import ru.spbu.apmath.nalisin.common_entities.MusicFile
import ru.spbu.apmath.nalisin.common_utils.GetAudioFormatUseCase
import ru.spbu.apmath.nalisin.common_utils.toTarsosDspAudioFormat
import java.io.ByteArrayInputStream
import java.io.File
import java.util.Collections.synchronizedList
import kotlin.math.log10
import kotlin.math.sqrt

@Inject
class LoudnessAnalyzerImpl(
    private val getAudioFormatUseCase: GetAudioFormatUseCase,
) : LoudnessAnalyzer {
    override fun getAverageLoudness(audioFilePath: String): Double {
        val file = File(audioFilePath)
        val audioFormat = getAudioFormatUseCase(audioFilePath)
        return getAverageLoudness(musicFile = MusicFile(audioData = file.readBytes(), format = audioFormat))
    }

    override fun getAverageLoudness(musicFile: MusicFile): Double {
        val inputStream =
            UniversalAudioInputStream(
                ByteArrayInputStream(musicFile.audioData),
                musicFile.format.toTarsosDspAudioFormat(),
            )
        val dispatcher = AudioDispatcher(inputStream, BUFFER_SIZE, OVERLAP)

        // Создание списка для сохранения всех значений RMS
        val rmsValues = synchronizedList(mutableListOf<Double>())

        // Создание обработчика аудио, который вычислит RMS каждого блока
        val processor = object : AudioProcessor {
            override fun process(audioEvent: AudioEvent?): Boolean {
                val buffer = audioEvent?.floatBuffer ?: return true
                var rmsSum = 0.0
                for (sample in buffer) {
                    rmsSum += sample * sample
                }
                val rms = sqrt(rmsSum / buffer.size)
                rmsValues.add(rms)
                return true // Обработка продолжается
            }

            override fun processingFinished() = Unit
        }

        dispatcher.addAudioProcessor(processor)

        // Запуск обработки
        dispatcher.run()

        // Вычисление среднего значения RMS от всех блоков
        val globalRMS = rmsValues.average()
        // Перевод RMS в децибелы
        val dB = 20 * log10(globalRMS)

        return dB
    }

    private companion object {
        const val BUFFER_SIZE = 2048
        const val OVERLAP = 0
    }
}
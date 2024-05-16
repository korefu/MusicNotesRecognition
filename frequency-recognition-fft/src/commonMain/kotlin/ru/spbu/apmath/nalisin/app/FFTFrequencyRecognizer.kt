package ru.spbu.apmath.nalisin.app

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.io.UniversalAudioInputStream
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.PitchProcessor
import me.tatarka.inject.annotations.Inject
import ru.spbu.apmath.nalisin.common_entities.MusicFile
import ru.spbu.apmath.nalisin.common_utils.GetAudioFormatUseCase
import ru.spbu.apmath.nalisin.common_utils.toTarsosDspAudioFormat
import ru.spbu.apmath.nalisin.frequency_recognition_api.FrequencyRecognizer
import java.io.ByteArrayInputStream
import java.io.File

@Inject
class FFTFrequencyRecognizer(
    private val getAudioFormatUseCase: GetAudioFormatUseCase,
) : FrequencyRecognizer {

    override fun recognizeFrequency(audioFilePath: String): Double? {
        val file = File(audioFilePath)
        return recognizeFrequency(
            musicFile = MusicFile(
                audioData = file.readBytes(),
                format = getAudioFormatUseCase(audioFilePath),
            )
        )
    }

    override fun recognizeFrequency(musicFile: MusicFile): Double? {
        return try {
            val inputStream =
                UniversalAudioInputStream(
                    ByteArrayInputStream(musicFile.audioData),
                    musicFile.format.toTarsosDspAudioFormat(),
                )
            val dispatcher = AudioDispatcher(inputStream, BUFFER_SIZE, OVERLAP)

            var detectedFrequency: Double? = null
            val pitchProcessor = PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                dispatcher.format.sampleRate,
                BUFFER_SIZE
            ) { result: PitchDetectionResult, _: AudioEvent ->
                val pitchInHz = result.pitch.toDouble()
                if (pitchInHz != -1.0) {
                    detectedFrequency = pitchInHz
                }
            }
            dispatcher.addAudioProcessor(pitchProcessor)
            dispatcher.run()
            return detectedFrequency
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private companion object {
        const val BUFFER_SIZE = 2048
        const val OVERLAP = 0
    }
}
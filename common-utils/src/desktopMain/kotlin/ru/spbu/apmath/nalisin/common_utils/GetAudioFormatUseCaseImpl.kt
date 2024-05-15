package ru.spbu.apmath.nalisin.common_utils

import me.tatarka.inject.annotations.Inject
import ru.spbu.apmath.nalisin.common_entities.UniversalAudioFormat
import java.io.File
import javax.sound.sampled.AudioSystem

@Inject
actual class GetAudioFormatUseCaseImpl : GetAudioFormatUseCase {

    actual override operator fun invoke(filePath: String): UniversalAudioFormat {
        return AudioSystem.getAudioFileFormat(File(filePath)).format.toUniversalAudioFormat()
    }
}
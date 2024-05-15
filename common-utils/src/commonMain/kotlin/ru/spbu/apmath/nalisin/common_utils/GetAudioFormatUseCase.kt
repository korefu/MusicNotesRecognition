package ru.spbu.apmath.nalisin.common_utils

import ru.spbu.apmath.nalisin.common_entities.UniversalAudioFormat

fun interface GetAudioFormatUseCase {

    operator fun invoke(filePath: String): UniversalAudioFormat
}

expect class GetAudioFormatUseCaseImpl : GetAudioFormatUseCase {

    override operator fun invoke(filePath: String): UniversalAudioFormat
}
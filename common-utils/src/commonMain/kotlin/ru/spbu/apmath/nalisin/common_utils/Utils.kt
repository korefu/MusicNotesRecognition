package ru.spbu.apmath.nalisin.common_utils

import be.tarsos.dsp.io.TarsosDSPAudioFormat
import ru.spbu.apmath.nalisin.common_entities.UniversalAudioFormat
import javax.sound.sampled.AudioFormat

/**
 * @author s.nalisin
 */
fun TarsosDSPAudioFormat.toUniversalAudioFormat(): UniversalAudioFormat {
    return UniversalAudioFormat(
        sampleRate = this.sampleRate,
        sampleSizeInBits = this.sampleSizeInBits,
        channels = this.channels,
        signed = this.encoding == AudioFormat.Encoding.PCM_SIGNED,
        bigEndian = this.isBigEndian,
    )
}

fun UniversalAudioFormat.toTarsosDspAudioFormat(): TarsosDSPAudioFormat {
    return TarsosDSPAudioFormat(
        /* sampleRate */ this.sampleRate,
        /* sampleSizeInBits */ this.sampleSizeInBits * channels,
        /* channels */ 1,
        /* signed */ this.signed,
        /* bigEndian */ this.bigEndian,
    )
}
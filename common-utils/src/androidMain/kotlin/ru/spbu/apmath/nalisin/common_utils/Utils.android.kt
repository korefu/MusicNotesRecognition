package ru.spbu.apmath.nalisin.common_utils

import android.media.AudioFormat
import android.os.Build
import ru.spbu.apmath.nalisin.common_entities.UniversalAudioFormat

fun AudioFormat.toUniversalAudioFormat(): UniversalAudioFormat {
    return UniversalAudioFormat(
        sampleRate = this.sampleRate.toFloat(),
        sampleSizeInBits = getBytesPerSample(this.encoding) * this.channelCount,
        channels = this.channelCount,
        signed = getSignedEncodings.contains(this.encoding),
        bigEndian = false,
    )
}

fun UniversalAudioFormat.toJvmAudioFormat(): AudioFormat {
    return AudioFormat.Builder()
        .setSampleRate(this.sampleRate.toInt())
        .setChannelIndexMask(
            when (this.channels) {
                1 -> AudioFormat.CHANNEL_IN_MONO
                2 -> AudioFormat.CHANNEL_IN_STEREO
                else -> throw IllegalArgumentException("Too many channels. Channels count: $channels")
            }
        )
        .setEncoding(
            when {
                this.bigEndian -> throw IllegalArgumentException("Convert bigEndian to littleEndian")

                this.sampleSizeInBits == 8 -> AudioFormat.ENCODING_PCM_8BIT

                this.sampleSizeInBits == 16 -> AudioFormat.ENCODING_PCM_16BIT

                this.sampleSizeInBits == 24 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    AudioFormat.ENCODING_PCM_24BIT_PACKED
                }

                this.sampleSizeInBits == 32 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    AudioFormat.ENCODING_PCM_32BIT
                }

                else -> throw IllegalArgumentException("Unable to find android encoding")
            }
        )
        .build()
}

private fun getBytesPerSample(audioFormat: Int): Int {
    return when (audioFormat) {
        AudioFormat.ENCODING_PCM_8BIT -> 1
        AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_IEC61937, AudioFormat.ENCODING_DEFAULT -> 2
        AudioFormat.ENCODING_PCM_24BIT_PACKED -> 3
        AudioFormat.ENCODING_PCM_FLOAT, AudioFormat.ENCODING_PCM_32BIT -> 4
        AudioFormat.ENCODING_INVALID -> throw IllegalArgumentException("Bad audio format $audioFormat")
        else -> throw IllegalArgumentException("Bad audio format $audioFormat")
    }
}

private val getSignedEncodings: List<Int> = buildList {
    add(AudioFormat.ENCODING_PCM_16BIT)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        add(AudioFormat.ENCODING_PCM_24BIT_PACKED)
        add(AudioFormat.ENCODING_PCM_32BIT)
    }
}
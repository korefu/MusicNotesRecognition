package ru.spbu.apmath.nalisin.common_entities

data class UniversalAudioFormat(
    val sampleRate: Float,
    val channels: Int,
    val sampleSizeInBits: Int,
    val signed: Boolean,
    val bigEndian: Boolean
)
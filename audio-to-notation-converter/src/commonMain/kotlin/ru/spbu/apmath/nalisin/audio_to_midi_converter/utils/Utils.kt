package ru.spbu.apmath.nalisin.audio_to_midi_converter.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun <A, B> Iterable<A>.parallelMap(transform: suspend (A) -> B): List<B> = coroutineScope {
    map { item -> async { transform(item) } }.awaitAll()
}

suspend fun <A, B> Iterable<A>.parallelMapIndexed(transform: suspend (Int, A) -> B): List<B> = coroutineScope {
    mapIndexed { index, item -> async { transform(index, item) } }.awaitAll()
}
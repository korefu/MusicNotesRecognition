package com.example.musicxml_writer

import ru.spbu.apmath.nalisin.common_entities.Measure

/**
 * @author s.nalisin
 */
interface MusicXmlCreator {

    fun getMusicXml(
        bpm: Int = 120,
        fifths: Int = 0,
        beats: Int = 4,
        beatType: Int = 4,
        measures: List<Measure>,
    ): String
}
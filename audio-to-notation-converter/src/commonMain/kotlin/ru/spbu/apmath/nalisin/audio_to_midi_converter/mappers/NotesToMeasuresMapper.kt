package ru.spbu.apmath.nalisin.audio_to_midi_converter.mappers

import ru.spbu.apmath.nalisin.common_entities.Measure
import ru.spbu.apmath.nalisin.common_entities.Note
import ru.spbu.apmath.nalisin.common_entities.TimeSignature

/**
 * @author s.nalisin
 */
object NotesToMeasuresMapper {

    fun List<Note>.toMeasures(timeSignature: TimeSignature): List<Measure> {
        val notes = this
        val barDuration = timeSignature.beatsPerBar.toDouble() / timeSignature.beatType
        val measures = mutableListOf<Measure>()
        val notesInCurrentMeasure = mutableListOf<Note>()
        var remainedBarDuration: Double = barDuration
        var measureId = 0
        notes.forEach { note ->
            notesInCurrentMeasure.add(note)
            remainedBarDuration -= (note.duration.getDurationInWholes())
            if (remainedBarDuration <= 0) {
                val size = notesInCurrentMeasure.fold(0.0) { acc, note -> acc + note.duration.getDurationInWholes() }
                measures.add(Measure(id = measureId, notes = notesInCurrentMeasure.toList(), size = size))
                measureId++
                notesInCurrentMeasure.clear()
                remainedBarDuration = barDuration
            }
        }
        if (notesInCurrentMeasure.isNotEmpty()) {
            val size = notesInCurrentMeasure.fold(0.0) { acc, note -> acc + note.duration.getDurationInWholes() }
            measures.add(Measure(id = measureId, notes = notesInCurrentMeasure.toList(), size = size))
        }
        return measures
    }
}
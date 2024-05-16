package com.example.musicxml_writer

import me.tatarka.inject.annotations.Inject
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.PrintOptions
import org.redundent.kotlin.xml.XmlVersion
import org.redundent.kotlin.xml.xml
import ru.spbu.apmath.nalisin.common_entities.Measure
import ru.spbu.apmath.nalisin.common_entities.Note

/**
 * @author s.nalisin
 */
@Inject
class MusicXmlCreatorImpl : MusicXmlCreator {

    override fun getMusicXml(bpm: Int, fifths: Int, beats: Int, beatType: Int, measures: List<Measure>): String {
        val musicXml = xml(root = "score-partwise", version = XmlVersion.V10, encoding = "UTF-8") {
            doctype(
                publicId = "-//Recordare//DTD MusicXML 4.0 Partwise//EN",
                systemId = "http://www.musicxml.org/dtds/partwise.dtd"
            )
            attribute("version", "4.0")
            "part-list" {
                "score-part" {
                    attribute("id", "P1")
                    "part-name" { -"Music" }
                }
            }
            "part" {
                attribute("id", "P1")
                measures.forEachIndexed { index, measure ->
                    "measure" {
                        attribute("number", "${measure.id}")
                        if (index == 0) {
                            setTonality(fifths = fifths, beats = beats, beatType = beatType)
                            setTimeSignature(bpm = bpm)
                        }
                        measure.notes.forEach { note ->
                            setNote(note = note)
                        }
                    }
                }
            }
        }
        return musicXml.toString(printOptions = PrintOptions(singleLineTextElements = true, useSelfClosingTags = true))
    }

    private fun Node.setTimeSignature(bpm: Int): Node {
        return "direction" {
            attribute("placement", "above")
            "direction-type" {
                "metronome" {
                    attributes(
                        "font-family" to "EngraverTextT",
                        "font-size" to "12",
                        "parentheses" to "no",
                    )
                    "beat-unit" { -"quarter" }
                    "per-minute" { -"$bpm" }
                }
            }
        }
    }

    private fun Node.setTonality(fifths: Int, beats: Int, beatType: Int): Node {
        return "attributes" {
            "divisions" { -"8" }
            "key" {
                "fifths" { -"$fifths" }
            }
            "time" {
                "beats" { -"$beats" }
                "beat-type" { -"$beatType" }
            }
            "clef" {
                "sign" { -"G" }
                "line" { -"2" }
            }
        }
    }

    private fun Node.setNote(note: Note): Node {
        return "note" {
            when (note) {
                is Note.Melodic -> {
                    "pitch" {
                        "step" { -"${note.name}" }
                        when (note.accidental) {
                            Note.Accidental.SHARP -> "alter" { -"1" }
                            Note.Accidental.FLAT -> "alter" { -"-1" }
                            Note.Accidental.NATURAL, Note.Accidental.NONE -> Unit
                        }
                        "octave" { -"${note.octave}" }
                    }
                }

                is Note.Rest -> {
                    "rest" {}
                }
            }
            "duration" { -"${note.duration.getDuration()}" }
            "type" { -note.duration.text }
            if (note.duration.isDotted) "dot" {}
            if (note is Note.Melodic) {
                if (note.accidental != Note.Accidental.NONE) {
                    "accidental" { -note.accidental.name.lowercase() }
                }
                if (note.isStaccato) {
                    "notations" {
                        "articulations" {
                            "staccato" {}
                        }
                    }
                }
            }
        }
    }
}
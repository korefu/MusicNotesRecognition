package ru.spbu.apmath.nalisin.common_entities

/**
 * @author s.nalisin
 */
sealed interface Note {
    val duration: Duration

    class Rest(override val duration: Duration) : Note

    class Melodic(
        override val duration: Duration,
        val octave: Int,
        val name: Name,
        val accidental: Accidental = Accidental.NONE,
        val beams: List<Beam> = emptyList(),
        val isStaccato: Boolean = false,
    ) : Note

    sealed class Duration(val isDotted: Boolean, val number: Int, val text: String) {

        fun getDuration(): Int {
            return if (isDotted) {
                (number * 1.5).toInt()
            } else number
        }

        fun getDurationInWholes(): Double = getDuration().toDouble() / 32

        class Whole(isDotted: Boolean = false) : Duration(isDotted = isDotted, number = 32, text = "whole")
        class Half(isDotted: Boolean = false) : Duration(isDotted = isDotted, number = 16, text = "half")
        class Quarter(isDotted: Boolean = false) : Duration(isDotted = isDotted, number = 8, text = "quarter")
        class Eighth(isDotted: Boolean = false) : Duration(isDotted = isDotted, number = 4, text = "eighth")
        class Sixteenth(isDotted: Boolean = false) : Duration(isDotted = isDotted, number = 2, text = "16th")
        class ThirtySecond(isDotted: Boolean = false) : Duration(isDotted = isDotted, number = 1, text = "32nd")
    }

    enum class Name(val index: Int) {
        C(0),
        D(2),
        E(4),
        F(5),
        G(7),
        A(9),
        B(11),
    }

    enum class Accidental {
        SHARP,
        FLAT,
        NATURAL,
        NONE
    }

    enum class Beam {
        BEGIN,
        CONTINUE,
        END,
    }
}
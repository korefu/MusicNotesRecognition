package ru.spbu.apmath.nalisin.app.components.main.recorder

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

/**
 * @author s.nalisin
 */
class StubRecorder : Recorder {
    override val state: Value<Recorder.State> = MutableValue(Recorder.State())

    override fun onRecordClick() = Unit

    override fun onPauseClick() = Unit

    override fun onStopClick() = Unit

    override fun onSaveClick() = Unit
}
package ru.spbu.apmath.nalisin.app.components.main.convertersettings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import ru.spbu.apmath.nalisin.audio_to_midi_converter.Settings as MusicXmlConverterSettings

class ConverterSettings(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val _state = MutableValue(State())
    val state: Value<State> = _state

    fun onBpmChange(bpm: String) {
        val value = bpm.toIntOrNull() ?: return
        _state.update { it.copy(settings = it.settings.copy(bpm = value)) }
    }

    fun onMedianFilterWindowSizeChange(medianFilterWindowSize: String) {
        val value = medianFilterWindowSize.toIntOrNull() ?: return
        _state.update { it.copy(settings = it.settings.copy(medianFilterWindowSize = value)) }
    }

    fun onMinDurationThresholdChange(minDurationThreshold: String) {
        val value = minDurationThreshold.toDoubleOrNull() ?: return
        _state.update { it.copy(settings = it.settings.copy(minDurationThreshold = value)) }
    }

    fun onFragmentDurationInMillisChange(fragmentDurationInMillis: String) {
        val value = fragmentDurationInMillis.toLongOrNull() ?: return
        _state.update { it.copy(settings = it.settings.copy(fragmentDurationInMillis = value)) }
    }

    fun onBeatsPerBarChange(beatsPerBar: String) {
        val value = beatsPerBar.toIntOrNull() ?: return
        _state.update {
            it.copy(settings = it.settings.copy(timeSignature = it.settings.timeSignature.copy(beatsPerBar = value)))
        }
    }

    data class State(val settings: MusicXmlConverterSettings = MusicXmlConverterSettings())
}
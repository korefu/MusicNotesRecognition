package ru.spbu.apmath.nalisin.app.components.main.recorder

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.spbu.apmath.nalisin.common_entities.MusicFile
import kotlin.coroutines.CoroutineContext

interface Recorder {
    val state: Value<State>

    fun onRecordClick()
    fun onPauseClick()
    fun onStopClick()
    fun onSaveClick()

    data class State(val isRecording: Boolean = false, val recordingTime: Int = 0)
}

class RecorderImpl(
    componentContext: ComponentContext,
    private val voiceRecorder: VoiceRecorder,
    mainContext: CoroutineContext = Dispatchers.Main,
    private val ioContext: CoroutineContext = Dispatchers.IO,
    private val onMusicFileRecorded: (MusicFile?) -> Unit,
) : ComponentContext by componentContext, Recorder {

    private val scope = coroutineScope(mainContext + SupervisorJob())
    private var timerJob: Job? = null

    private val _state = MutableValue(Recorder.State())
    override val state: Value<Recorder.State> = _state

    override fun onRecordClick() {
        _state.update { it.copy(isRecording = true) }
        scope.launch(ioContext) { voiceRecorder.start() }
        timerJob = scope.launch(ioContext) {
            while (state.value.isRecording) {
                delay(1000)
                _state.update { it.copy(recordingTime = it.recordingTime + 1) }
            }
        }
    }

    override fun onPauseClick() {
        timerJob?.cancel()
        voiceRecorder.pause()
        _state.update { it.copy(isRecording = false) }
    }

    override fun onStopClick() {
        timerJob?.cancel()
        voiceRecorder.stop()
        _state.update { it.copy(isRecording = false, recordingTime = 0) }
    }

    override fun onSaveClick() {
        timerJob?.cancel()
        _state.update { it.copy(isRecording = false) }
        scope.launch {
            val musicFile = voiceRecorder.getAudio()
            onMusicFileRecorded(musicFile)
        }
    }

}

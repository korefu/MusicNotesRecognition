package ru.spbu.apmath.nalisin.app.components.main.fileimport

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.darkrockstudios.libraries.mpfilepicker.MPFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.spbu.apmath.nalisin.common_entities.MusicFile
import ru.spbu.apmath.nalisin.common_utils.GetAudioFormatUseCase
import kotlin.coroutines.CoroutineContext

class FileImport(
    componentContext: ComponentContext,
    private val onMusicFileImported: (MusicFile) -> Unit = {},
    private val getAudioFormatUseCase: GetAudioFormatUseCase,
    mainContext: CoroutineContext = Dispatchers.Main,
    private val ioContext: CoroutineContext = Dispatchers.IO,
    ) : ComponentContext by componentContext {

    private val scope = coroutineScope(mainContext + SupervisorJob())
    private val file = MutableStateFlow<MusicFile?>(null)

    val fileTypes = listOf("wav")

    init {
        scope.launch {
            file.collect { file ->
                file?.let { onMusicFileImported(it) }
            }
        }
    }

    private val _state = MutableValue(State())
    val state: Value<State> = _state

    fun importFileButtonCLicked() {
        _state.update { it.copy(showFilePicker = true) }
    }

    fun fileReceived(platformFile: MPFile<Any>?) {
        _state.update { it.copy(showFilePicker = false) }
        if (platformFile != null) {
            scope.launch(ioContext) {
                val audioFormat = getAudioFormatUseCase(platformFile.path)
                file.update { MusicFile(audioData = platformFile.getFileByteArray(), format = audioFormat) }
            }
        }
    }

    data class State(val showFilePicker: Boolean = false)
}
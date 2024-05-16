package ru.spbu.apmath.nalisin.app

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import ru.spbu.apmath.nalisin.app.components.main.MainContent
import ru.spbu.apmath.nalisin.app.di.createMainDiComponent
import java.awt.Dimension

fun main() {
    val lifecycle = LifecycleRegistry()
    val diComponent = runOnUiThread {
        createMainDiComponent(componentContext = DefaultComponentContext(lifecycle = lifecycle))
    }
    application {
        val density = LocalDensity.current
        val maxWidth = with(density) { 250.dp.toPx().toInt() }
        val startHeight = with(density) { 350.dp.toPx().toInt() }
        val state = rememberWindowState(width = maxWidth.dp, height = startHeight.dp)
        Window(
            onCloseRequest = ::exitApplication,
            title = "MusicNotesRecognition",
            state = state,
        ) {
            window.maximumSize = Dimension(maxWidth, Int.MAX_VALUE)
            MainContent(component = diComponent.mainComponent)
        }
    }
}

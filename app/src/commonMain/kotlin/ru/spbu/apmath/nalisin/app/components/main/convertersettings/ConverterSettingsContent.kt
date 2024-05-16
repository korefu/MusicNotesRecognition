package ru.spbu.apmath.nalisin.app.components.main.convertersettings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.spbu.apmath.nalisin.app.theme.AppTheme

@Composable
fun ConverterSettingsContent(
    converterSettings: ConverterSettings,
    modifier: Modifier = Modifier,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val state by converterSettings.state.subscribeAsState()

    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Настройки алгоритма",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    ConverterSettingsItem(
                        uiModel = ConverterSettingsItemUiModel(name = "BPM"),
                        model = ConverterSettingsItemModel(
                            settingsValue = state.settings.bpm.toString(),
                            onValueChange = converterSettings::onBpmChange,
                        )
                    )
                    ConverterSettingsItem(
                        uiModel = ConverterSettingsItemUiModel(name = "Размер в четвертях"),
                        model = ConverterSettingsItemModel(
                            settingsValue = state.settings.timeSignature.beatsPerBar.toString(),
                            onValueChange = converterSettings::onBeatsPerBarChange,
                        )
                    )
                    ConverterSettingsItem(
                        uiModel = ConverterSettingsItemUiModel(name = "Окно медианного фильтра"),
                        model = ConverterSettingsItemModel(
                            settingsValue = state.settings.medianFilterWindowSize.toString(),
                            onValueChange = converterSettings::onMedianFilterWindowSizeChange,
                        )
                    )
                    ConverterSettingsItem(
                        uiModel = ConverterSettingsItemUiModel(name = "Мин. длительность ноты"),
                        model = ConverterSettingsItemModel(
                            settingsValue = state.settings.minDurationThreshold.toString(),
                            onValueChange = converterSettings::onMinDurationThresholdChange,
                        )
                    )
                    ConverterSettingsItem(
                        uiModel = ConverterSettingsItemUiModel(name = "Размер окна (мс)"),
                        model = ConverterSettingsItemModel(
                            settingsValue = state.settings.fragmentDurationInMillis.toString(),
                            onValueChange = converterSettings::onFragmentDurationInMillisChange,
                        )
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ConverterSettingsContentPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ConverterSettingsContent(
                converterSettings = ConverterSettings(
                    componentContext = DefaultComponentContext(
                        lifecycle = LifecycleRegistry()
                    )
                )
            )
        }
    }
}
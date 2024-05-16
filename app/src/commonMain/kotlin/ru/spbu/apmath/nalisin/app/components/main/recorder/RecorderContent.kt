package ru.spbu.apmath.nalisin.app.components.main.recorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.spbu.apmath.nalisin.app.components.main.common.MaxRelWidthButton
import ru.spbu.apmath.nalisin.app.theme.AppTheme

@Composable
fun RecorderContent(
    modifier: Modifier = Modifier,
    recorder: Recorder,
) {
    val state by recorder.state.subscribeAsState()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = modifier.padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Запись звука",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(text = formatTime(state.recordingTime), fontSize = 24.sp, modifier = Modifier.padding(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.width(IntrinsicSize.Min)) {
                if (state.isRecording) {
                    MaxRelWidthButton(onClick = recorder::onPauseClick, text = "Пауза")
                } else {
                    MaxRelWidthButton(onClick = recorder::onRecordClick, text = "Запись")
                }
                MaxRelWidthButton(onClick = recorder::onStopClick, text = "Очистить")
                MaxRelWidthButton(onClick = recorder::onSaveClick, text = "Сохранить")
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    return "${(seconds / 60).toString().padStart(2, '0')}:${(seconds % 60).toString().padStart(2, '0')}"
}

@Preview
@Composable
fun RecorderContentPreview() {
    AppTheme {
        RecorderContent(recorder = StubRecorder())
    }
}
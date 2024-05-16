package ru.spbu.apmath.nalisin.app.components.main.convertersettings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.spbu.apmath.nalisin.app.theme.AppTheme

/**
 * @author s.nalisin
 */
@Composable
fun ConverterSettingsItem(uiModel: ConverterSettingsItemUiModel, model: ConverterSettingsItemModel) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiModel.name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp).clickable { isExpanded = !isExpanded },
            )
            OutlinedTextField(
                modifier = Modifier.width(100.dp).padding(0.dp, 8.dp, 8.dp, 0.dp),
                value = model.settingsValue,
                onValueChange = model.onValueChange,
                singleLine = true,
                shape = MaterialTheme.shapes.small,
            )
        }
        AnimatedVisibility(visible = isExpanded && uiModel.description.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp),
                text = uiModel.description,
            )
        }
    }
}

@Composable
@Preview
fun ConverterSettingsItemPreview() {
    AppTheme {
        ConverterSettingsItem(
            uiModel = ConverterSettingsItemUiModel(
                name = "Settings Name",
                description = "Lorem ipsum lskdjfk sdjfbksj dfbksbd jfsd lkjsdflskjd ldsjfnvlksdj dfjnvlkjdnf fsd",
            ),
            model = ConverterSettingsItemModel(
                onValueChange = {},
                settingsValue = "120"
            )
        )
    }
}
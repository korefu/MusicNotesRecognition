package ru.spbu.apmath.nalisin.app.components.main.convertersettings

/**
 * @author s.nalisin
 */
data class ConverterSettingsItemModel(
    val settingsValue: String,
    val onValueChange: (String) -> Unit,
)
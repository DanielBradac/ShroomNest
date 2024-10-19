package cz.bradacd.shroomnest.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cz.bradacd.shroomnest.HumiditySettings
import cz.bradacd.shroomnest.ManuallyConfigurable
import cz.bradacd.shroomnest.PeriodicallyConfigurable
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> ModeSelector(
    modeClass: Class<T>,
    pushIsLoading: Boolean,
    mode: T,
    onModeChange: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(mode) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                if (!pushIsLoading) {
                    expanded = !expanded
                }
            }
        ) {
            OutlinedTextField(
                label = { Text(text = "Mode") },
                value = selectedMode.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    if (!pushIsLoading) {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                },
                enabled = !pushIsLoading,
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                modeClass.enumConstants?.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.name) },
                        onClick = {
                            selectedMode = item
                            expanded = false
                            onModeChange(selectedMode)
                        },
                        enabled = !pushIsLoading
                    )
                }
            }
        }
    }
}

@Composable
fun ManuallyConfigurable.ManualSettings(
    onDeviceToggleChange: (Boolean) -> Unit,
    pushIsLoading: Boolean
) {
    Row {
        Switch(
            checked = deviceOn(),
            onCheckedChange = { onDeviceToggleChange(it) },
            enabled = !pushIsLoading
        )
    }
}

@Composable
fun PeriodicallyConfigurable.PeriodicSettings(
    onWaitPerChange: (Int?) -> Unit,
    onRunPerChange: (Int?) -> Unit,
    pushIsLoading: Boolean,
    deviceName: String
) {
    OutlinedTextField(
        modifier = Modifier.padding(bottom = 8.dp),
        value = waitPer?.toString() ?: "",
        onValueChange = { newValue ->
            if (newValue.isNotBlank()) {
                val intValue = newValue.toIntOrNull()
                if (intValue != null) {
                    onWaitPerChange(intValue)
                }
            } else {
                onWaitPerChange(null)
            }
        },
        enabled = !pushIsLoading,
        label = { Text("Waiting period (seconds)") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
    OutlinedTextField(
        value = runPer?.toString() ?: "",
        onValueChange = { newValue ->
            if (newValue.isNotBlank()) {
                val intValue = newValue.toIntOrNull()
                if (intValue != null) {
                    onRunPerChange(intValue)
                }
            } else {
                onRunPerChange(null)
            }
        },
        enabled = !pushIsLoading,
        label = { Text("Runtime period (seconds)") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )

    Text(
        modifier = Modifier.padding(top = 16.dp),
        text = getPeriodicInfo(deviceName)
    )
}

fun PeriodicallyConfigurable.getPeriodicInfo(
    deviceName: String
): AnnotatedString {
    return buildAnnotatedString {
        append("$deviceName will be turned ")
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.Bold)
        ) {
            append(if (deviceOn()) "ON" else "OFF")
        }
        append(" in:\n")

        var remainingTime = if (deviceOn()) {
            (runPer ?: 0) - runTime
        } else {
            (waitPer ?: 0) - waitTime
        }

        if (remainingTime <= 0) {
            append("0 sec")
            return@buildAnnotatedString
        }

        withStyle(
            style = SpanStyle(fontStyle = FontStyle.Italic)
        ) {
            val hours = remainingTime / 3600
            if (hours > 0) {
                append("$hours h ")
                remainingTime %= 3600
            }

            val minutes = remainingTime / 60
            if (minutes > 0) {
                append("$minutes min ")
                remainingTime %= 60
            }

            if (remainingTime > 0) {
                append("$remainingTime sec")
            }
        }
    }
}

@Composable
fun HumiditySettings.AutomaticSettings(
    onHumidityRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    pushIsLoading: Boolean
) {
    RangeSlider(
        modifier = Modifier.padding(vertical = 8.dp),
        value = humidityRange,
        steps = 100,
        onValueChange = { newRange ->
            val start = newRange.start.roundToInt().toFloat()
            val end = newRange.endInclusive.roundToInt().toFloat()
            onHumidityRangeChange(start..end)
        },
        valueRange = 0f..100f,
        enabled = !pushIsLoading
    )
    Text(
        text = "Humidity range: ${humidityRange.start.toInt()} % - ${humidityRange.endInclusive.toInt()} %"
    )
}
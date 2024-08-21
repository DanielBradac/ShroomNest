package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.shroomnest.ui.Headline
import cz.bradacd.shroomnest.viewmodel.HumiditySettings
import cz.bradacd.shroomnest.viewmodel.HumiditySettingsMode
import cz.bradacd.shroomnest.viewmodel.HumidityViewModel
import kotlin.math.roundToInt

@Composable
fun HumidityScreen(viewModel: HumidityViewModel = viewModel()) {
    val fetchIsLoading by viewModel.fetchIsLoading.collectAsState()
    val pushIsLoading by viewModel.pushIsLoading.collectAsState()
    val humiditySettings by viewModel.humiditySettings.collectAsState()
    val errorData by viewModel.error.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            Headline("Humidity Manager")

            if (humiditySettings != null) {
                humiditySettings!!.HumiditySettingsOptions(
                    onModeChange = { newValue -> viewModel.updateMode(newValue) },
                    onHumidityRangeChange = { newRange -> viewModel.updateHumidityRange(newRange) },
                    onHumidifierOnChange = { newValue -> viewModel.updateHumidifierOn(newValue) },
                    onWaitPerChange = { newValue -> viewModel.updateWaitPer(newValue) },
                    onRunPerChange = { newValue -> viewModel.updateRunPer(newValue) },
                    pushIsLoading = pushIsLoading
                )
            }

            if (fetchIsLoading) {
                Text(text = "Loading server data...")
            }

            if (pushIsLoading) {
                Text(text = "Uploading data...")
            }

            if (errorData.isNotBlank()) {
                Text(text = "Error: $errorData")
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        Column(modifier = Modifier.align(if (isLandscape) Alignment.BottomEnd else Alignment.BottomStart)) {
            Button(
                enabled = !fetchIsLoading && !pushIsLoading && humiditySettings != null,
                onClick = {
                    viewModel.pushHumiditySettings(context)
                }) {
                Text("Push settings")
            }

            Button(
                enabled = !fetchIsLoading && !pushIsLoading,
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    viewModel.fetchHumiditySettings()
                }) {
                Text("Pull settings")
            }
        }
    }
}

@Composable
fun HumiditySettings.HumiditySettingsOptions(
    onModeChange: (HumiditySettingsMode) -> Unit,
    onHumidityRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onHumidifierOnChange: (Boolean) -> Unit,
    onWaitPerChange: (Int?) -> Unit,
    onRunPerChange: (Int?) -> Unit,
    pushIsLoading: Boolean
) {
    Column {
        ModeSelector(onModeChange, pushIsLoading)
        when (mode) {
            HumiditySettingsMode.Automatic -> {
                AutomaticSettings(
                    onHumidityRangeChange = onHumidityRangeChange,
                    pushIsLoading = pushIsLoading
                )
            }

            HumiditySettingsMode.Periodic -> {
                PeriodicSettings(
                    onWaitPerChange = onWaitPerChange,
                    onRunPerChange = onRunPerChange,
                    pushIsLoading = pushIsLoading
                )
            }

            HumiditySettingsMode.Manual -> {
                ManualSettings(
                    onHumidifierOnChange = onHumidifierOnChange,
                    pushIsLoading = pushIsLoading
                )
            }
        }

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = buildAnnotatedString {
                append("Humidifier is: ")
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = if (humidifierOn) Color.Green else Color.Red
                    ),
                ) {
                    append(if (humidifierOn) "ON" else "OFF")
                }
            }
        )
    }
}

@Composable
fun HumiditySettings.ManualSettings(
    onHumidifierOnChange: (Boolean) -> Unit,
    pushIsLoading: Boolean
) {
    Row {
        Switch(
            checked = humidifierOn,
            onCheckedChange = { onHumidifierOnChange(it) },
            enabled = !pushIsLoading
        )
    }
}

@Composable
fun HumiditySettings.PeriodicSettings(
    onWaitPerChange: (Int?) -> Unit,
    onRunPerChange: (Int?) -> Unit,
    pushIsLoading: Boolean
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
        text = getPeriodicInfo()
    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HumiditySettings.ModeSelector(
    onModeChange: (HumiditySettingsMode) -> Unit,
    pushIsLoading: Boolean
) {
    val modes = HumiditySettingsMode.entries
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
                label = { Text("Mode") },
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
                modes.forEach { item ->
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

fun HumiditySettings.getPeriodicInfo(): AnnotatedString {

    return buildAnnotatedString {
        append("Humidifier will be turned ")
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.Bold)
        ) {
            append(if (humidifierOn) "OFF" else "ON")
        }
        append(" in:\n")

        var remainingTime = if (humidifierOn) {
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

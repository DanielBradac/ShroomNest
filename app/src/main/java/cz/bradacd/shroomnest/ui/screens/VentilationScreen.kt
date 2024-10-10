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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.shroomnest.ui.Headline
import cz.bradacd.shroomnest.viewmodel.VentilationSettings
import cz.bradacd.shroomnest.viewmodel.VentilationSettingsMode
import cz.bradacd.shroomnest.viewmodel.VentilationViewModel

@Composable
fun VentilationScreen(viewModel: VentilationViewModel = viewModel()) {
    val fetchIsLoading by viewModel.fetchIsLoading.collectAsState()
    val pushIsLoading by viewModel.pushIsLoading.collectAsState()
    val ventilationSettings by viewModel.ventilationSettings.collectAsState()
    val errorData by viewModel.error.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape =
        LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            Headline("Ventilation Manager")

            if (ventilationSettings != null) {
                ventilationSettings!!.VentilationSettingsOptions(
                    onModeChange = { newValue -> viewModel.updateMode(newValue) },
                    onFanOnChange = { newValue -> viewModel.updateFanOn(newValue) },
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
                enabled = !fetchIsLoading && !pushIsLoading && ventilationSettings != null,
                onClick = {
                    viewModel.pushVentilationSettings(context)
                }) {
                Text("Push settings")
            }

            Button(
                enabled = !fetchIsLoading && !pushIsLoading,
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    viewModel.fetchVentilationSettings()
                }) {
                Text("Pull settings")
            }
        }
    }
}

@Composable
fun VentilationSettings.VentilationSettingsOptions(
    onModeChange: (VentilationSettingsMode) -> Unit,
    onFanOnChange: (Boolean) -> Unit,
    onWaitPerChange: (Int?) -> Unit,
    onRunPerChange: (Int?) -> Unit,
    pushIsLoading: Boolean
) {
    Column {
        ModeSelector(onModeChange, pushIsLoading)
        when (mode) {
            VentilationSettingsMode.Periodic -> {
                PeriodicSettings(
                    onWaitPerChange = onWaitPerChange,
                    onRunPerChange = onRunPerChange,
                    pushIsLoading = pushIsLoading
                )
            }

            VentilationSettingsMode.Manual -> {
                ManualSettings(
                    onFanOnChange = onFanOnChange,
                    pushIsLoading = pushIsLoading
                )
            }
        }

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = buildAnnotatedString {
                append("Fan is: ")
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = if (fanOn) Color.Green else Color.Red
                    ),
                ) {
                    append(if (fanOn) "ON" else "OFF")
                }
            }
        )
    }
}

@Composable
fun VentilationSettings.ManualSettings(
    onFanOnChange: (Boolean) -> Unit,
    pushIsLoading: Boolean
) {
    Row {
        Switch(
            checked = fanOn,
            onCheckedChange = { onFanOnChange(it) },
            enabled = !pushIsLoading
        )
    }
}

@Composable
fun VentilationSettings.PeriodicSettings(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentilationSettings.ModeSelector(
    onModeChange: (VentilationSettingsMode) -> Unit,
    pushIsLoading: Boolean
) {
    val modes = VentilationSettingsMode.entries
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

fun VentilationSettings.getPeriodicInfo(): AnnotatedString {
    return buildAnnotatedString {
        append("Fan will be turned ")
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.Bold)
        ) {
            append(if (fanOn) "OFF" else "ON")
        }
        append(" in:\n")

        var remainingTime = if (fanOn) {
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
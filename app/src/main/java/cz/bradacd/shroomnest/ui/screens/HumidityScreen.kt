package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.shroomnest.HumiditySettings
import cz.bradacd.shroomnest.HumiditySettingsMode
import cz.bradacd.shroomnest.ui.AutomaticSettings
import cz.bradacd.shroomnest.ui.Headline
import cz.bradacd.shroomnest.ui.ManualSettings
import cz.bradacd.shroomnest.ui.ModeSelector
import cz.bradacd.shroomnest.ui.PeriodicSettings
import cz.bradacd.shroomnest.viewmodel.HumidityViewModel
import kotlinx.coroutines.launch

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Headline("Humidity Manager")

            if (humiditySettings != null) {
                humiditySettings!!.HumiditySettingsOptions(
                    onModeChange = { newValue -> viewModel.updateMode(newValue) },
                    onHumidityRangeChange = { newRange -> viewModel.updateHumidityRange(newRange) },
                    onHumidifierOnChange = { newValue -> viewModel.updateHumidifierOn(newValue) },
                    onWaitPerChange = { newValue -> viewModel.updateWaitPer(newValue) },
                    onRunPerChange = { newValue -> viewModel.updateRunPer(newValue) },
                    onRunWithFanChange = { newValue -> viewModel.updateRunWithFan(newValue) },
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
    onRunWithFanChange: (Boolean) -> Unit,
    pushIsLoading: Boolean
) {
    Column {
        ModeSelector(
            modeClass = HumiditySettingsMode::class.java,
            pushIsLoading = pushIsLoading,
            onModeChange = onModeChange,
            mode = mode
        )

        when (mode) {
            HumiditySettingsMode.Automatic -> {
                FanSyncToggle(onRunWithFanChange)
                AutomaticSettings(
                    onHumidityRangeChange = onHumidityRangeChange,
                    pushIsLoading = pushIsLoading
                )
            }

            HumiditySettingsMode.Periodic -> {
                FanSyncToggle(onRunWithFanChange)
                PeriodicSettings(
                    onWaitPerChange = onWaitPerChange,
                    onRunPerChange = onRunPerChange,
                    pushIsLoading = pushIsLoading,
                    deviceName = "Humidifier"
                )
            }

            HumiditySettingsMode.Manual -> {
                ManualSettings(
                    onDeviceToggleChange = onHumidifierOnChange,
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
fun HumiditySettings.FanSyncToggle(onRunWithFanChange: (Boolean) -> Unit) {
    Row {
        Switch(
            checked = runWithFan,
            onCheckedChange = {
                onRunWithFanChange(it)
            }
        )

        Text(
            text = "Fan parallel run: ",
            modifier = Modifier.padding(top = 12.dp, start = 8.dp)
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = if (runWithFan) Color.Green else Color.Red
                    ),
                ) {
                    append(if (runWithFan) "ON" else "OFF")
                }
            }
        )
    }
}

package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.shroomnest.VentilationSettings
import cz.bradacd.shroomnest.VentilationSettingsMode
import cz.bradacd.shroomnest.ui.Headline
import cz.bradacd.shroomnest.ui.ManualSettings
import cz.bradacd.shroomnest.ui.ModeSelector
import cz.bradacd.shroomnest.ui.PeriodicSettings
import cz.bradacd.shroomnest.viewmodel.VentilationViewModel

@Composable
fun VentilationScreen(viewModel: VentilationViewModel = viewModel()) {
    val fetchIsLoading by viewModel.fetchIsLoading.collectAsState()
    val pushIsLoading by viewModel.pushIsLoading.collectAsState()
    val ventilationSettings by viewModel.ventilationSettings.collectAsState()
    val errorData by viewModel.error.collectAsState()
    val context = LocalContext.current
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
        ModeSelector(
            modeClass = VentilationSettingsMode::class.java,
            pushIsLoading = pushIsLoading,
            onModeChange = onModeChange,
            mode = mode
        )
        when (mode) {
            VentilationSettingsMode.Periodic -> {
                PeriodicSettings(
                    onWaitPerChange = onWaitPerChange,
                    onRunPerChange = onRunPerChange,
                    pushIsLoading = pushIsLoading,
                    deviceName = "Fan"
                )
            }

            VentilationSettingsMode.Manual -> {
                ManualSettings(
                    onDeviceToggleChange = onFanOnChange,
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


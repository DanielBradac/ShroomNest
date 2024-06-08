package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.shroomnest.ui.Headline
import cz.bradacd.shroomnest.viewmodel.HumiditySettings
import cz.bradacd.shroomnest.viewmodel.HumidityViewModel
import kotlin.math.roundToInt

@Composable
fun HumidityScreen(viewModel: HumidityViewModel = viewModel()) {
    val fetchIsLoading by viewModel.fetchIsLoading.collectAsState()
    val pushIsLoading by viewModel.pushIsLoading.collectAsState()
    val humiditySettings by viewModel.humiditySettings.collectAsState()
    val errorData by viewModel.error.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {

        }
        Column(modifier = Modifier.fillMaxSize()) {
            Headline("Humidity Manager")

            if (humiditySettings != null) {
                humiditySettings!!.HumiditySettingsOptions(
                    onAutomaticChange = { newValue -> viewModel.updateAutomatic(newValue) },
                    onHumidityRangeChange = { newRange -> viewModel.updateHumidityRange(newRange) },
                    onHumidifierOnChange = { newValue -> viewModel.updateHumidifierOn(newValue) }
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

        Column(modifier = Modifier.align(Alignment.BottomStart)) {
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
    onAutomaticChange: (Boolean) -> Unit,
    onHumidityRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onHumidifierOnChange: (Boolean) -> Unit
) {
    Row {
        Switch(
            checked = automatic,
            onCheckedChange = { onAutomaticChange(it) }
        )
        Text(
            text = if (automatic) "Automatic" else "Manual",
            modifier = Modifier.padding(top = 10.dp, start = 16.dp)
        )
    }

    if (automatic) {
        RangeSlider(
            value = humidityRange,
            steps = 100,
            onValueChange = { newRange ->
                val start = newRange.start.roundToInt().toFloat()
                val end = newRange.endInclusive.roundToInt().toFloat()
                onHumidityRangeChange(start..end)
            },
            valueRange = 0f..100f
        )
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "Humidity range: ${humidityRange.start.toInt()} % - ${humidityRange.endInclusive.toInt()} %"
        )

        Text(text = buildAnnotatedString {
            append("Humidifier is: ")
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = if (humidifierOn) Color.Green else Color.Red
                ),
            ) {
                append(if (humidifierOn) "ON" else "OFF")
            }
        })

    } else {
        Row {
            Switch(
                checked = humidifierOn,
                onCheckedChange = { onHumidifierOnChange(it) }
            )
            Text(
                text = if (humidifierOn) "Humidifier ON" else "Humidifier OFF",
                modifier = Modifier.padding(top = 10.dp, start = 16.dp)
            )
        }
    }
}

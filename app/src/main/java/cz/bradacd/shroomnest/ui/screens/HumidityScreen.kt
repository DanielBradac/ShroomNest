package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bradacd.shroomnest.ui.Headline
import kotlin.math.roundToInt

@Composable
fun HumidityScreen() {
    var automatic by remember { mutableStateOf(true) }
    var humidifierOn by remember { mutableStateOf(true) }
    var humidityRange by remember { mutableStateOf(0f..100f) }

    Column(modifier = Modifier.padding(16.dp)) {
        Headline("Humidity Manager")

        Row {
            Switch(checked = automatic, onCheckedChange = { automatic = it })
            Text(
                text = if (automatic) "Automatic" else "Manual",
                modifier = Modifier.padding(top = 10.dp, start = 16.dp)
            )
        }

        if (automatic) {
            RangeSlider(
                value = humidityRange,
                steps = 100,
                onValueChange = {
                    humidityRange =
                        it.start.roundToInt().toFloat()..it.endInclusive.roundToInt().toFloat()
                },
                valueRange = 0f..100f
            )
            Text(text = "Humidity range: ${humidityRange.start.toInt()} % - ${humidityRange.endInclusive.toInt()} %")

        } else {
            Row {
                Switch(checked = humidifierOn, onCheckedChange = { humidifierOn = it })
                Text(
                    text = if (humidifierOn) "Humidifier ON" else "Humidifier OFF",
                    modifier = Modifier.padding(top = 10.dp, start = 16.dp)
                )
            }
        }

        Button(
            modifier = Modifier
                .padding(top = 16.dp),
            onClick = {

            }) {
            Text("Upload changes")
        }

    }
}
package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import cz.bradacd.shroomnest.ui.Headline
import cz.bradacd.shroomnest.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val statusIsLoading by viewModel.statusIsLoading.collectAsState()
    val statusData by viewModel.statusData.collectAsState()
    val statusError by viewModel.statusError.collectAsState()

    val logIsLoading by viewModel.logIsLoading.collectAsState()
    val logData by viewModel.logData.collectAsState()
    val logError by viewModel.logError.collectAsState()

    val isLandscape =
        LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Headline("Shroom Nest")

            if (statusData != null) {
                Text(text = "Temperature: ${statusData?.temperature ?: "Data not found"} °C")
                Text(text = "Humidity: ${statusData?.humidity ?: "Data not found"} %")
            }

            if (logData != null) {
                Text(logData.toString())
            }

            if (statusError.isNotBlank()) {
                Text(text = "Status fetch error: $statusError")
            }

            if (statusIsLoading) {
                Text(text = "Loading sensor data...")
            }

            if (logIsLoading) {
                Text(text = "Loading log data...")
            }

            if (logError.isNotBlank()) {
                Text(text = "Log fetch error: $logError")
            }

            Spacer(modifier = Modifier.weight(1f)) // Spacer to push the content above the buttons
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
                .align(if (isLandscape) Alignment.BottomEnd else Alignment.BottomStart),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.weight(1f)) // Spacer to push the buttons to the bottom

            Button(
                modifier = Modifier
                    .padding(top = 16.dp),
                enabled = !statusIsLoading,
                onClick = {
                    viewModel.fetchStatus()
                },
            ) {
                Text("Update status")
            }
            Row {
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    enabled = !logIsLoading,
                    onClick = {
                        viewModel.fetchLog()
                    },
                ) {
                    Text("Update log")
                }

                Button(
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp),
                    enabled = !logIsLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xffdf0000)
                    ),
                    onClick = {
                        viewModel.purgeLog()
                    },
                ) {
                    Text("Delete log")
                }
            }

        }
    }
}
package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.bradacd.shroomnest.ui.Headline
import cz.bradacd.shroomnest.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.shroomnest.apiclient.StatusResponse
import cz.bradacd.shroomnest.ui.LogViewer
import cz.bradacd.shroomnest.utils.sorted

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current

    val statusIsLoading by viewModel.statusIsLoading.collectAsState()
    val statusData by viewModel.statusData.collectAsState()
    val statusError by viewModel.statusError.collectAsState()

    val logIsLoading by viewModel.logIsLoading.collectAsState()
    val logData by viewModel.logData.collectAsState()
    val logError by viewModel.logError.collectAsState()

    val sortBySeverity by viewModel.sortBySeverity.collectAsState()

    val isLandscape =
        LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Text("Landscape mode not supported")
        return
    }

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
            Headline("Shroom Nest")

            if (statusIsLoading) {
                Text(text = "Loading sensor data...")
            }

            if (statusError.isNotBlank()) {
                Text(text = "Status fetch error: $statusError")
            }

            if (statusData != null) {
                StatusInfo(statusData = statusData)
            }

            if (logIsLoading) {
                Text(text = "Loading log data...")
            }

            if (logError.isNotBlank()) {
                Text(text = "Log fetch error: $logError")
            }

            // Scrollable content
            if (!logData.isNullOrEmpty()) {
                Box(modifier = Modifier.weight(1f)) {
                    LogViewer(
                        logData?.sorted(sortBySeverity) ?: emptyList(),
                        sortBySeverity
                    ) { newSortBySeverity ->
                        viewModel.toggleSortMethod(newSortBySeverity)
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Fixed buttons at the bottom
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
            ) {
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    enabled = !statusIsLoading,
                    onClick = {
                        viewModel.fetchStatus()
                    },
                ) {
                    Text("Update status")
                }
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                ) {
                    Button(
                        enabled = !logIsLoading,
                        onClick = {
                            viewModel.fetchLog()
                        },
                    ) {
                        Text("Update log")
                    }

                    Button(
                        modifier = Modifier
                            .padding(start = 8.dp),
                        enabled = !logIsLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53935)
                        ),
                        onClick = {
                            viewModel.purgeLog(context)
                        },
                    ) {
                        Text("Delete log")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusInfo(statusData: StatusResponse?) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Thermostat,
                contentDescription = "Temperature Icon",
                tint = primaryColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(fontSize = 20.sp, text = "Temperature:", modifier = Modifier.weight(1.1f))
            Text(
                fontSize = 20.sp,
                text = "${statusData?.temperature ?: "Data not found"} °C",
                modifier = Modifier.weight(1f)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = "Humidity Icon",
                tint = primaryColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(fontSize = 20.sp, text = "Humidity:", modifier = Modifier.weight(1.1f))
            Text(
                fontSize = 20.sp,
                text = "${statusData?.humidity ?: "Data not found"} %",
                modifier = Modifier.weight(1f)
            )
        }
    }
}
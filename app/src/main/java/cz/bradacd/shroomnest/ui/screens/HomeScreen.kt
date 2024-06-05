package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bradacd.shroomnest.ui.Headline
import cz.bradacd.shroomnest.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val isLoading by viewModel.isLoading.collectAsState()
    val statusData by viewModel.statusData.collectAsState()
    val errorData by viewModel.error.collectAsState()

    Box(modifier = Modifier
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

            if (errorData.isNotBlank()) {
                Text(text = "Error: $errorData")
            }

            if (isLoading) {
                Text(text = "Loading data...")
            }

            Spacer(modifier = Modifier.weight(1f)) // Spacer to push the button to the bottom
        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(top = 16.dp),
            enabled = !isLoading,
            onClick = {
                viewModel.fetchStatus()
            },
        ) {
            Text("Update status")
        }
    }
}
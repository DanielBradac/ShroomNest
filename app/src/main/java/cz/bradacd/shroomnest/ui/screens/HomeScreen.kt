package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bradacd.shroomnest.ui.Headline
import cz.bradacd.shroomnest.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val statusData = viewModel.statusData.collectAsState()
    val errorData = viewModel.error.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
        Headline("Shroom Nest")

        if (statusData.value != null) {
            Text(text = "Temperature: ${statusData.value?.temperature?: "Data not found"} °C")
            Text(text = "Humidity: ${statusData.value?.humidity?: "Data not found"} %")
        }

        if (errorData.value.isNotBlank()) {
            Text(text = "Error: ${errorData.value}")
        }

        if (statusData.value == null && errorData.value == "") {
            Text(text = "Loading data...")
        } else {
            Button(
                modifier = Modifier
                    .padding(top = 16.dp),
                onClick = {
                    viewModel.updateStatus()
                }) {
                Text("Update")
            }
        }
    }
}
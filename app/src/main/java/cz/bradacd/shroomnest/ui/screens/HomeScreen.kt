package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cz.bradacd.shroomnest.ui.Headline
import cz.bradacd.shroomnest.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val statusData = viewModel.statusData.collectAsState()
    val errorData = viewModel.error.collectAsState()
    Column {
        Headline("Shroom Nest")
        Text(text = "Temperature: ${statusData.value.temperature}")
        Text(text = "Humidity: ${statusData.value.humidity}")

        if (errorData.value.isNotBlank()) {
            Text(text = "Error: ${errorData.value}")
        }
    }
}
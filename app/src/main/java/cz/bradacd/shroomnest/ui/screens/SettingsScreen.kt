package cz.bradacd.shroomnest.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import cz.bradacd.shroomnest.ui.Headline
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.shroomnest.viewmodel.Settings
import cz.bradacd.shroomnest.viewmodel.SettingsViewModel
import cz.bradacd.shroomnest.viewmodel.getSettings


@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    var apiRoot by remember { mutableStateOf(getSettings(context).apiRoot) }
    var humidifierIp by remember { mutableStateOf(getSettings(context).humidifierIp) }
    var fanIp by remember { mutableStateOf(getSettings(context).fanIp) }
    val pushIsLoading by viewModel.pushIsLoading.collectAsState()
    val errorData by viewModel.error.collectAsState()
    val isLandscape =
        LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

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
            Headline("Settings")

            OutlinedTextField(
                modifier = Modifier.padding(bottom = 8.dp),
                value = apiRoot,
                onValueChange = { apiRoot = it },
                label = { Text("API root") }
            )

            OutlinedTextField(
                value = humidifierIp,
                onValueChange = { humidifierIp = it },
                label = { Text("Humidifier IP") }
            )

            OutlinedTextField(
                value = fanIp,
                onValueChange = { fanIp = it },
                label = { Text("Fan IP") }
            )

            if (pushIsLoading) {
                Text(text = "Uploading IP settings to server...")
            }

            if (errorData.isNotBlank()) {
                Text(text = "Error: $errorData")
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        Button(
            modifier = Modifier
                .align(if (isLandscape) Alignment.BottomEnd else Alignment.BottomStart)
                .padding(top = 16.dp),
            onClick = {
                viewModel.saveSettings(context, Settings(apiRoot, humidifierIp, fanIp))
                Toast.makeText(context, "Settings saved to local storage", Toast.LENGTH_SHORT)
                    .show()
            }) {
            Text("Save Settings")
        }
    }
}





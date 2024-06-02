package cz.bradacd.shroomnest.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import cz.bradacd.shroomnest.settings.SettingsManager
import cz.bradacd.shroomnest.ui.Headline
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bradacd.shroomnest.settings.Settings


@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val settingsManager = SettingsManager(context)
    var apiRoute by remember { mutableStateOf(settingsManager.getSettings().apiRoot) }

    Column {
        Headline("Settings")

        OutlinedTextField(
            value = apiRoute,
            onValueChange = { apiRoute = it },
            label = { Text("API root") }
        )

        Button(
            modifier = Modifier
                .padding(top = 16.dp),
            onClick = {
                settingsManager.saveSettings(Settings(apiRoute))
                Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show()
            }) {
            Text("Save Settings")
        }

    }
}




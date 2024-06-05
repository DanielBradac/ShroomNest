package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bradacd.shroomnest.ui.Headline

@Composable
fun LightScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Headline("Light Manager")
            Spacer(modifier = Modifier.weight(1f))
        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(top = 16.dp),
            onClick = {

            }) {
            Text("Upload changes")
        }
    }
}
package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bradacd.shroomnest.ui.Headline

@Composable
fun LightScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Headline("Light")
    }
}
package cz.bradacd.shroomnest.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.bradacd.shroomnest.ui.Headline

@Composable
fun HomeScreen() {
    Column {
        Headline("Shroom Nest")
        Text(text = "Temperature: ")
        Text(text = "Humidity: ")
    }
}
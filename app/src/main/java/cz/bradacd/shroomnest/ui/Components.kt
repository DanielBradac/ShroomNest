package cz.bradacd.shroomnest.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Headline(text: String) {
    Text(
        text = text,
        fontSize = 32.sp,
        modifier = Modifier
            .padding(bottom = 32.dp)
    )
}
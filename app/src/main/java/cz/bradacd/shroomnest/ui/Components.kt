package cz.bradacd.shroomnest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.bradacd.shroomnest.utils.LogLevel
import cz.bradacd.shroomnest.utils.LogMessage
import cz.bradacd.shroomnest.utils.formatInstant
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

@Composable
fun Headline(text: String) {
    Text(
        text = text,
        fontSize = 32.sp,
        modifier = Modifier
            .padding(bottom = 32.dp)
    )
}

@Composable
fun RowScope.TableCell(
    headerText: String = "",
    text: String,
    weight: Float
) {
    Column(
        modifier = Modifier
            .weight(weight)
    ) {
        if (headerText.isNotBlank()) {
            Text(
                text = headerText,
                modifier = Modifier.padding(4.dp),
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            modifier = Modifier.padding(4.dp),
            text = text
        )
    }
}

@Composable
fun TableRow(logMessage: LogMessage) = logMessage.run {
    // Each cell of a column must have the same weight.

    val column1Weight = .3f // 30%
    val column2Weight = .7f // 70%
    val color = when (logMessage.level) {
        LogLevel.INFO -> Color(0xFFBBDEFB)
        LogLevel.WARNING -> Color(0xFFFFAB91)
        LogLevel.ERROR -> Color(0xFFEF9A9A)
    }

    Row(
        Modifier
            .fillMaxWidth()
            .background(color = color)
            .drawBehind {
                val borderSize = 1.dp.toPx()
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(0f, size.height - borderSize),
                    size = Size(size.width, borderSize)
                )
            }
    ) {
        TableCell(text = timestamp.formatInstant(), weight = column1Weight)
        TableCell(headerText = header, text = message, weight = column2Weight)
    }
}

@Composable
fun LogViewer(
    logMessages: List<LogMessage>,
    sortBySeverity: Boolean,
    onSortChange: (Boolean) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(top = 8.dp)
    ) {

        Row {
            Text(
                text = "Log",
                fontSize = 24.sp,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Sort by:",
                modifier = Modifier.padding(top = 10.dp, start = 8.dp)
            )

            Text(
                text = if (sortBySeverity) "Severity" else "Time",
                modifier = Modifier.padding(top = 10.dp, start = 8.dp, end = 8.dp)
            )

            Switch(
                checked = sortBySeverity,
                onCheckedChange = {
                    coroutineScope.launch {
                        onSortChange(it)
                        //listState.scrollToItem(0)
                        listState.animateScrollToItem(0)
                    }
                }
            )
        }

        LazyColumn(state = listState, modifier = Modifier.border(1.dp, color = Color.Black)) {
            items(logMessages.size) { rowIndex ->
                TableRow(logMessages[rowIndex])
            }
        }
    }
}
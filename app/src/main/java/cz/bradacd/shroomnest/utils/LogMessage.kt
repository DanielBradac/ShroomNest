package cz.bradacd.shroomnest.utils

import cz.bradacd.shroomnest.apiclient.LogResp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class LogLevel {
    ERROR, WARNING, INFO
}

private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

data class LogMessage(
    val level: LogLevel,
    val message: String,
    val header: String,
    val timestamp: Instant?
)

fun LogResp.toMessages(): List<LogMessage> {
    val infos = info?.map {
        LogMessage(LogLevel.INFO, it.message?:"", it.header?:"", it.timestamp?.toInstant())
    } ?: emptyList()

    val warnings = warning?.map {
        LogMessage(LogLevel.WARNING, it.message?:"", it.header?:"", it.timestamp?.toInstant())
    } ?: emptyList()

    val errors = error?.map {
        LogMessage(LogLevel.ERROR, it.message?:"", it.header?:"", it.timestamp?.toInstant())
    } ?: emptyList()

    return infos + warnings + errors
}

fun List<LogMessage>.sorted(sortBySeverity: Boolean): List<LogMessage> {
    if (sortBySeverity) {
        return this.sortedWith(compareBy(LogMessage::level, LogMessage::timestamp))
    }
    return this.sortedBy { it.timestamp }
}

fun String.toInstant(): Instant? {
    val localDateTime = LocalDateTime.parse(this, formatter)
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant()
}

fun Instant?.formatInstant(): String {
    if (this == null) {
        return ""
    }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())
    return formatter.format(this)
}

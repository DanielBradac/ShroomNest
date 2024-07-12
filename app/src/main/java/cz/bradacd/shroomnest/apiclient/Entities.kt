package cz.bradacd.shroomnest.apiclient

import com.google.gson.annotations.SerializedName
import cz.bradacd.shroomnest.utils.LogMessage


data class StatusResponse(
    @SerializedName("humudity") val humidity: Float? = null,
    @SerializedName("temperature") val temperature: Float? = null,
)

data class HumiditySettingsResponse(
    @SerializedName("rangeFrom") val rangeFrom: Float? = null,
    @SerializedName("rangeTo") val rangeTo: Float? = null,
    @SerializedName("mode") val mode: String = "",
    @SerializedName("humidifierOn") val humidifierOn: Boolean? = null,
)

data class HumiditySettingsRequest(
    @SerializedName("rangeFrom") val rangeFrom: Float? = null,
    @SerializedName("rangeTo") val rangeTo: Float? = null,
    @SerializedName("mode") val mode: String? = null,
    @SerializedName("humidifierOn") val humidifierOn: Boolean? = null,
)

data class IPSettingsReqResp(
    @SerializedName("humidifierIp") val humidifierIp: String? = null,
)

data class LogResp(
    @SerializedName("info") val info: List<LogMessageResp>? = emptyList(),
    @SerializedName("warning") val warning: List<LogMessageResp>? = emptyList(),
    @SerializedName("error") val error: List<LogMessageResp>? = emptyList(),
)

data class LogMessageResp(
    @SerializedName("message") val message: String? = null,
    @SerializedName("header") val header: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null,
)

fun String?.getHumidifierModeBoolean(): Boolean? {
    return when (this) {
        "auto" -> true
        "manual" -> false
        else -> null
    }
}

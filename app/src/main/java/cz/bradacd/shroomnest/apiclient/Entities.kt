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
    @SerializedName("waitPer") val waitPer: Int? = null,
    @SerializedName("runPer") val runPer: Int? = null,
    @SerializedName("waitTime") val waitTime: Int? = null,
    @SerializedName("runTime") val runTime: Int? = null,
    @SerializedName("runWithFan") val runWithFan: Boolean? = null
)

data class VentilationSettingsRequest(
    @SerializedName("mode") val mode: String? = null,
    @SerializedName("fanOn") val fanOn: Boolean? = null,
    @SerializedName("waitPer") val waitPer: Int? = null,
    @SerializedName("runPer") val runPer: Int? = null,
)

data class VentilationSettingsResponse(
    @SerializedName("mode") val mode: String = "",
    @SerializedName("fanOn") val fanOn: Boolean? = null,
    @SerializedName("waitPer") val waitPer: Int? = null,
    @SerializedName("runPer") val runPer: Int? = null,
    @SerializedName("waitTime") val waitTime: Int? = null,
    @SerializedName("runTime") val runTime: Int? = null,
)

data class HumiditySettingsRequest(
    @SerializedName("rangeFrom") val rangeFrom: Float? = null,
    @SerializedName("rangeTo") val rangeTo: Float? = null,
    @SerializedName("mode") val mode: String? = null,
    @SerializedName("humidifierOn") val humidifierOn: Boolean? = null,
    @SerializedName("waitPer") val waitPer: Int? = null,
    @SerializedName("runPer") val runPer: Int? = null,
    @SerializedName("runWithFan") val runWithFan: Boolean? = null
)

data class IPSettingsReqResp(
    @SerializedName("humidifierIp") val humidifierIp: String? = null,
    @SerializedName("fanIp") val fanIp: String? = null,
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

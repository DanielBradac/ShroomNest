package cz.bradacd.shroomnest.apiclient

import com.google.gson.annotations.SerializedName


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

fun String?.getHumidifierModeBoolean(): Boolean? {
    return when (this) {
        "auto" -> true
        "manual" -> false
        else -> null
    }
}

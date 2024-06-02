package cz.bradacd.shroomnest.apiclient

import com.google.gson.annotations.SerializedName

data class StatusResponse(
    @SerializedName("humudity") val humidity: Float? = null,
    @SerializedName("temperature") val temperature: Float? = null,
    @SerializedName("error") val error: String = ""
)
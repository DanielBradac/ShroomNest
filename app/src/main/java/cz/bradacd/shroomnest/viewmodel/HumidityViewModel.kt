package cz.bradacd.shroomnest.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bradacd.shroomnest.InvalidInputException
import cz.bradacd.shroomnest.apiclient.HumiditySettingsRequest
import cz.bradacd.shroomnest.apiclient.HumiditySettingsResponse
import cz.bradacd.shroomnest.apiclient.RetrofitInstance
import cz.bradacd.shroomnest.apiclient.apiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call

enum class HumiditySettingsMode(val code: String) {
    Automatic("auto"),
    Periodic("period"),
    Manual("manual")
}

data class HumiditySettings(
    var humidifierOn: Boolean,
    var humidityRange: ClosedFloatingPointRange<Float>,
    var mode: HumiditySettingsMode,
    var waitPer: Int?,
    var runPer: Int?,
    var waitTime: Int,
    var runTime: Int
)

class HumidityViewModel : ViewModel() {
    private val _humiditySettings: MutableStateFlow<HumiditySettings?> = MutableStateFlow(null)
    val humiditySettings: StateFlow<HumiditySettings?> = _humiditySettings

    private val _error: MutableStateFlow<String> = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _fetchIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val fetchIsLoading: StateFlow<Boolean> = _fetchIsLoading

    private val _pushIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val pushIsLoading: StateFlow<Boolean> = _pushIsLoading

    init {
        fetchHumiditySettings()
    }

    fun updateMode(newMode: HumiditySettingsMode) {
        _humiditySettings.value?.let {
            _humiditySettings.value = it.copy(mode = newMode)
        }
    }

    fun updateHumidityRange(newRange: ClosedFloatingPointRange<Float>) {
        _humiditySettings.value?.let {
            _humiditySettings.value = it.copy(humidityRange = newRange)
        }
    }

    fun updateHumidifierOn(newValue: Boolean) {
        _humiditySettings.value?.let {
            _humiditySettings.value = it.copy(humidifierOn = newValue)
        }
    }

    fun updateWaitPer(newValue: Int?) {
        _humiditySettings.value?.let {
            _humiditySettings.value = it.copy(waitPer = newValue)
        }
    }

    fun updateRunPer(newValue: Int?) {
        _humiditySettings.value?.let {
            _humiditySettings.value = it.copy(runPer = newValue)
        }
    }

    fun fetchHumiditySettings() {
        _humiditySettings.value = null
        _error.value = ""
        _fetchIsLoading.value = true

        viewModelScope.launch {
            val call: Call<HumiditySettingsResponse>? =
                RetrofitInstance.apiService?.getHumiditySettings()

            apiCall(
                call,
                onSuccess = { response ->
                    _fetchIsLoading.value = false
                    val responseData: HumiditySettingsResponse? = response.body()
                    if (responseData != null) {
                        _humiditySettings.value = responseData.toSettings()
                    } else {
                        _error.value = "Response body couldn't be parsed properly."
                    }
                },
                onError = { error ->
                    _fetchIsLoading.value = false
                    _error.value = error.message ?: "Unknown error"
                }
            )
        }
    }

    fun pushHumiditySettings(context: Context) {
        _error.value = ""
        _pushIsLoading.value = true

        if (humiditySettings.value == null) {
            _error.value = "Humidity settings is null"
            return
        }

        viewModelScope.launch {
            val call: Call<HumiditySettingsResponse>? =
                RetrofitInstance.apiService?.updateHumiditySettings(humiditySettings.value!!.toRequest())

            apiCall(
                call,
                onSuccess = { response ->
                    _pushIsLoading.value = false
                    val responseData: HumiditySettingsResponse? = response.body()
                    if (responseData != null) {
                        _humiditySettings.value = responseData.toSettings()
                        Toast.makeText(context, "Humidity settings uploaded", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        _error.value = "Response body couldn't be parsed properly."
                    }
                },
                onError = { error ->
                    _pushIsLoading.value = false
                    _error.value = error.message ?: "Unknown error"
                }
            )
        }
    }

}

fun HumiditySettingsResponse.toSettings(): HumiditySettings {
    return HumiditySettings(
        humidifierOn = this.humidifierOn ?: false,
        humidityRange = ((this.rangeFrom ?: 0f)..(this.rangeTo ?: 0f)),
        waitPer = this.waitPer ?: 0,
        runPer = this.runPer ?: 0,
        waitTime = this.waitTime ?: 0,
        runTime = this.runTime ?: 0,
        mode = when(this.mode) {
            HumiditySettingsMode.Automatic.code -> HumiditySettingsMode.Automatic
            HumiditySettingsMode.Periodic.code -> HumiditySettingsMode.Periodic
            HumiditySettingsMode.Manual.code -> HumiditySettingsMode.Manual
            else -> throw InvalidInputException("Unknown humidifier settings mode ${this.mode}")
        }
    )
}

fun HumiditySettings.toRequest(): HumiditySettingsRequest {
    return when (this.mode) {
        HumiditySettingsMode.Automatic -> {
            HumiditySettingsRequest(
                mode = HumiditySettingsMode.Automatic.code,
                rangeFrom = this.humidityRange.start,
                rangeTo = this.humidityRange.endInclusive
            )
        }
        HumiditySettingsMode.Periodic -> {
            HumiditySettingsRequest(
                mode = HumiditySettingsMode.Periodic.code,
                waitPer = this.waitPer ?: 0,
                runPer = this.runPer ?: 0
            )
        }
        HumiditySettingsMode.Manual -> {
            HumiditySettingsRequest(
                mode = HumiditySettingsMode.Manual.code,
                humidifierOn = this.humidifierOn
            )
        }
    }
}
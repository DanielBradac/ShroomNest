package cz.bradacd.shroomnest.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bradacd.shroomnest.InvalidInputException
import cz.bradacd.shroomnest.apiclient.HumiditySettingsRequest
import cz.bradacd.shroomnest.apiclient.HumiditySettingsResponse
import cz.bradacd.shroomnest.apiclient.RetrofitInstance
import cz.bradacd.shroomnest.apiclient.VentilationSettingsRequest
import cz.bradacd.shroomnest.apiclient.VentilationSettingsResponse
import cz.bradacd.shroomnest.apiclient.apiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call

enum class VentilationSettingsMode(val code: String) {
    Periodic("period"),
    Manual("manual")
}

data class VentilationSettings(
    var fanOn: Boolean,
    var mode: VentilationSettingsMode,
    var waitPer: Int?,
    var runPer: Int?,
    var waitTime: Int,
    var runTime: Int
)

class VentilationViewModel : ViewModel() {
    private val _ventilationSettings: MutableStateFlow<VentilationSettings?> = MutableStateFlow(null)
    val ventilationSettings: StateFlow<VentilationSettings?> = _ventilationSettings

    private val _error: MutableStateFlow<String> = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _fetchIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val fetchIsLoading: StateFlow<Boolean> = _fetchIsLoading

    private val _pushIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val pushIsLoading: StateFlow<Boolean> = _pushIsLoading

    init {
        fetchVentilationSettings()
    }

    fun updateMode(newMode: VentilationSettingsMode) {
        _ventilationSettings.value?.let {
            _ventilationSettings.value = it.copy(mode = newMode)
        }
    }

    fun updateFanOn(newValue: Boolean) {
        _ventilationSettings.value?.let {
            _ventilationSettings.value = it.copy(fanOn = newValue)
        }
    }

    fun updateWaitPer(newValue: Int?) {
        _ventilationSettings.value?.let {
            _ventilationSettings.value = it.copy(waitPer = newValue)
        }
    }

    fun updateRunPer(newValue: Int?) {
        _ventilationSettings.value?.let {
            _ventilationSettings.value = it.copy(runPer = newValue)
        }
    }

    fun fetchVentilationSettings() {
        _ventilationSettings.value = null
        _error.value = ""
        _fetchIsLoading.value = true

        viewModelScope.launch {
            val call: Call<VentilationSettingsResponse>? =
                RetrofitInstance.apiService?.getVentilationSettings()

            apiCall(
                call,
                onSuccess = { response ->
                    _fetchIsLoading.value = false
                    val responseData: VentilationSettingsResponse? = response.body()
                    if (responseData != null) {
                        _ventilationSettings.value = responseData.toSettings()
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

    fun pushVentilationSettings(context: Context) {
        _error.value = ""
        _pushIsLoading.value = true

        if (ventilationSettings.value == null) {
            _error.value = "Ventilation settings is null"
            return
        }

        viewModelScope.launch {
            val call: Call<VentilationSettingsResponse>? =
                RetrofitInstance.apiService?.updateVentilationSettings(ventilationSettings.value!!.toRequest())

            apiCall(
                call,
                onSuccess = { response ->
                    _pushIsLoading.value = false
                    val responseData: VentilationSettingsResponse? = response.body()
                    if (responseData != null) {
                        _ventilationSettings.value = responseData.toSettings()
                        Toast.makeText(context, "Ventilation settings uploaded", Toast.LENGTH_SHORT)
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

private fun VentilationSettingsResponse.toSettings(): VentilationSettings {
    return VentilationSettings(
        fanOn = this.fanOn ?: false,
        waitPer = this.waitPer ?: 0,
        runPer = this.runPer ?: 0,
        waitTime = this.waitTime ?: 0,
        runTime = this.runTime ?: 0,
        mode = when(this.mode) {
            VentilationSettingsMode.Periodic.code -> VentilationSettingsMode.Periodic
            VentilationSettingsMode.Manual.code -> VentilationSettingsMode.Manual
            else -> throw InvalidInputException("Unknown ventilation settings mode ${this.mode}")
        }
    )
}

private fun VentilationSettings.toRequest(): VentilationSettingsRequest {
    return when (this.mode) {
        VentilationSettingsMode.Periodic -> {
            VentilationSettingsRequest(
                mode = HumiditySettingsMode.Periodic.code,
                waitPer = this.waitPer ?: 0,
                runPer = this.runPer ?: 0
            )
        }
        VentilationSettingsMode.Manual -> {
            VentilationSettingsRequest(
                mode = HumiditySettingsMode.Manual.code,
                fanOn = this.fanOn
            )
        }
    }
}
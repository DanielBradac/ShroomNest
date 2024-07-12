package cz.bradacd.shroomnest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bradacd.shroomnest.apiclient.LogResp
import cz.bradacd.shroomnest.apiclient.RetrofitInstance
import cz.bradacd.shroomnest.apiclient.StatusResponse
import cz.bradacd.shroomnest.apiclient.apiCall
import cz.bradacd.shroomnest.utils.LogMessage
import cz.bradacd.shroomnest.utils.toMessages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call

class HomeViewModel : ViewModel() {
    private val _statusData: MutableStateFlow<StatusResponse?> = MutableStateFlow(null)
    val statusData: StateFlow<StatusResponse?> = _statusData

    private val _statusError: MutableStateFlow<String> = MutableStateFlow("")
    val statusError: StateFlow<String> = _statusError

    private val _statusIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val statusIsLoading: StateFlow<Boolean> = _statusIsLoading

    private val _logData: MutableStateFlow<List<LogMessage>?> = MutableStateFlow(null)
    val logData: StateFlow<List<LogMessage>?> = _logData

    private val _logError: MutableStateFlow<String> = MutableStateFlow("")
    val logError: StateFlow<String> = _logError

    private val _logIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val logIsLoading: StateFlow<Boolean> = _logIsLoading

    init {
        fetchStatus()
        fetchLog()
    }

    fun fetchStatus() {
        _statusData.value = null
        _statusError.value = ""
        _statusIsLoading.value = true

        viewModelScope.launch {
            val call: Call<StatusResponse>? = RetrofitInstance.apiService?.getStatus()
            apiCall(
                call,
                onSuccess = { response ->
                    _statusIsLoading.value = false
                    val responseData: StatusResponse? = response.body()
                    if (responseData != null) {
                        _statusData.value = responseData
                    } else {
                        _statusError.value = "Response body couldn't be parsed properly."
                    }
                },
                onError = { error ->
                    _statusIsLoading.value = false
                    _statusError.value = error.message ?: "Unknown error"
                }
            )
        }
    }

    fun fetchLog() {
        callLogService(RetrofitInstance.apiService?.getLogs())
    }

    fun purgeLog() {
        callLogService(RetrofitInstance.apiService?.purgeLogs())
    }

    private fun callLogService(apiCall: Call<LogResp>?) {
        _logData.value = null
        _logError.value = ""
        _logIsLoading.value = true

        viewModelScope.launch {
            val call: Call<LogResp>? = apiCall
            apiCall(
                call,
                onSuccess = { response ->
                    _logIsLoading.value = false
                    val responseData: LogResp? = response.body()
                    if (responseData != null) {
                        _logData.value = responseData.toMessages()
                    } else {
                        _logError.value = "Response body couldn't be parsed properly."
                    }
                },
                onError = { error ->
                    _logIsLoading.value = false
                    _logError.value = error.message ?: "Unknown error"
                }
            )
        }
    }
}

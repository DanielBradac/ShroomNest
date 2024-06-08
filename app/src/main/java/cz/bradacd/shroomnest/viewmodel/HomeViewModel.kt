package cz.bradacd.shroomnest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bradacd.shroomnest.apiclient.RetrofitInstance
import cz.bradacd.shroomnest.apiclient.StatusResponse
import cz.bradacd.shroomnest.apiclient.apiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call

class HomeViewModel : ViewModel() {
    private val _statusData: MutableStateFlow<StatusResponse?> = MutableStateFlow(null)
    val statusData: StateFlow<StatusResponse?> = _statusData

    private val _error: MutableStateFlow<String> = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchStatus()
    }

    fun fetchStatus() {
        _statusData.value = null
        _error.value = ""
        _isLoading.value = true

        viewModelScope.launch {
            val call: Call<StatusResponse>? = RetrofitInstance.apiService?.getStatus()
            apiCall(
                call,
                onSuccess = { response ->
                    _isLoading.value = false
                    val responseData: StatusResponse? = response.body()
                    if (responseData != null) {
                        _statusData.value = responseData
                    } else {
                        _error.value = "Response body couldn't be parsed properly."
                    }
                },
                onError = { error ->
                    _isLoading.value = false
                    _error.value = error.message ?: "Unknown error"
                }
            )
        }
    }
}

package cz.bradacd.shroomnest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bradacd.shroomnest.apiclient.RetrofitInstance
import cz.bradacd.shroomnest.apiclient.StatusResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        if (RetrofitInstance.apiService == null) {
            _error.value = "Retrofit client not initialised"
            return
        }

        viewModelScope.launch {
            val call: Call<StatusResponse> = RetrofitInstance.apiService!!.getStatus()
            call.enqueue(object : Callback<StatusResponse> {
                override fun onResponse(
                    call: Call<StatusResponse>,
                    response: Response<StatusResponse>
                ) {
                    _isLoading.value = false
                    if (!response.isSuccessful) {
                        _error.value =
                            "Unable to retrieve status, check API root setting. Response:\n ${response.raw()}"
                        return
                    }

                    val responseData: StatusResponse? = response.body()
                    if (responseData == null) {
                        _error.value = "Response body couldn't be parsed properly."
                        return
                    }
                    _statusData.value = responseData
                }

                override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                    _isLoading.value = false
                    _error.value = t.message ?: "Unknown error"
                }
            })

        }
    }
}

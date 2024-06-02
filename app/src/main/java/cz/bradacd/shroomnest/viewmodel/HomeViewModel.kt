package cz.bradacd.shroomnest.viewmodel

import android.util.Log
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

// TODO tady to asi předělat na ty fetche, co mám u cocktail mastera, těm flows nerozumím
// TODO nějaký rozumný error handeling a přidat placeholdery
// TODo fetchuje se to pokaždé nebo jen na začátku?

class HomeViewModel : ViewModel() {
    private val _statusData: MutableStateFlow<StatusResponse> = MutableStateFlow(StatusResponse())
    val statusData: StateFlow<StatusResponse> = _statusData

    private val _error: MutableStateFlow<String> = MutableStateFlow("")
    val error: StateFlow<String> = _error

    init {
        fetchStatus()
    }

    private fun fetchStatus() {
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
                    if (response.isSuccessful) {
                        val responseData: StatusResponse? = response.body()
                        if (responseData != null) {
                            _statusData.value = responseData
                            if (responseData.error.isNotBlank()) {
                                _error.value = responseData.error
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                    _error.value = t.message ?: "Unknown error"
                }
            })
        }

    }
}
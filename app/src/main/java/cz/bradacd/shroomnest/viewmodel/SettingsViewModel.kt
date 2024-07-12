package cz.bradacd.shroomnest.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import cz.bradacd.shroomnest.apiclient.IPSettingsReqResp
import cz.bradacd.shroomnest.apiclient.RetrofitInstance
import cz.bradacd.shroomnest.apiclient.apiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call

data class Settings(
    @SerializedName("apiRoot") val apiRoot: String,
    @SerializedName("humidifierIp") val humidifierIp: String
)

private val gson = Gson()

class SettingsViewModel : ViewModel() {

    private val _error: MutableStateFlow<String> = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _pushIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val pushIsLoading: StateFlow<Boolean> = _pushIsLoading

    fun saveSettings(context: Context, newSettings: Settings) {
        // Save to local storage
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        gson.toJson(newSettings)
        editor.putString("settings", Gson().toJson(newSettings))
        editor.apply()
        RetrofitInstance.init(newSettings.apiRoot)

        // Upload to server
        _error.value = ""
        _pushIsLoading.value = true

        viewModelScope.launch {
            val call: Call<IPSettingsReqResp>? =
                RetrofitInstance.apiService?.updateIPSettings(IPSettingsReqResp(newSettings.humidifierIp))

            apiCall(
                call,
                onSuccess = { response ->
                    _pushIsLoading.value = false
                    val responseData: IPSettingsReqResp? = response.body()
                    if (responseData == null) {
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

fun getSettings(context: Context): Settings {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    val settingsJson = sharedPreferences.getString("settings", null)
    return if (settingsJson != null) {
        gson.fromJson(settingsJson, Settings::class.java)
    } else {
        Settings("", "")
    }
}
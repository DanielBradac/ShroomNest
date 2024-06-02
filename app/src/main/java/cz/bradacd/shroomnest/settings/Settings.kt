package cz.bradacd.shroomnest.settings

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import cz.bradacd.shroomnest.apiclient.RetrofitInstance

data class Settings(
    @SerializedName("apiRoot") val apiRoot: String
)

class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    private val gson = Gson()
    fun saveSettings(newSettings: Settings) {
        val editor = sharedPreferences.edit()
        gson.toJson(newSettings)
        editor.putString("settings", Gson().toJson(newSettings))
        editor.apply()

        RetrofitInstance.init(newSettings.apiRoot)
    }

    fun getSettings(): Settings {
        val settingsJson = sharedPreferences.getString("settings", null)
        return if (settingsJson != null) {
            gson.fromJson(settingsJson, Settings::class.java)
        } else {
            Settings("")
        }
    }
}

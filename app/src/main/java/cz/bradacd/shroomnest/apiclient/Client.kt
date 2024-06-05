package cz.bradacd.shroomnest.apiclient

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


object RetrofitInstance {
    private var baseURL = ""

    private lateinit var retrofit: Retrofit
    var apiService: ApiService? = null

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .callTimeout(10, TimeUnit.SECONDS)
        .build()

    fun init(apiRoot: String) {
        try {
            baseURL = "http://$apiRoot/"
            retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            apiService = retrofit.create(ApiService::class.java)
        } catch (e: Exception) {
            Log.w("Retrofit API","Unable to init retrofit client: ${e.message}")
        }
    }
}

interface ApiService {
    @GET("status")
    fun getStatus() : Call<StatusResponse>

    @GET("getHumiditySettings")
    fun getHumiditySettings() : Call<HumiditySettingsResponse>

    @POST("updateHumiditySettings")
    fun updateHumiditySettings(@Body req: HumiditySettingsRequest) : Call<HumiditySettingsResponse>
}
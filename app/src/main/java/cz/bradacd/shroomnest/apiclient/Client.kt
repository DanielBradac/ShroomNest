package cz.bradacd.shroomnest.apiclient

import android.util.Log
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object RetrofitInstance {
    private var baseURL = ""

    private lateinit var retrofit: Retrofit
    var apiService: ApiService? = null

    fun init(apiRoot: String) {
        try {
            baseURL = "http://$apiRoot/"
            retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
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
}
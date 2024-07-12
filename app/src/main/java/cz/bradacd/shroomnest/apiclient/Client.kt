package cz.bradacd.shroomnest.apiclient

import android.util.Log
import cz.bradacd.shroomnest.APICallException
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
            Log.w("Retrofit API", "Unable to init retrofit client: ${e.message}")
        }
    }
}

interface ApiService {
    @GET("status")
    fun getStatus(): Call<StatusResponse>

    @GET("getHumiditySettings")
    fun getHumiditySettings(): Call<HumiditySettingsResponse>

    @POST("updateHumiditySettings")
    fun updateHumiditySettings(@Body req: HumiditySettingsRequest): Call<HumiditySettingsResponse>

    @POST("updateIPSettings")
    fun updateIPSettings(@Body req: IPSettingsReqResp): Call<IPSettingsReqResp>

    @GET("getLogs")
    fun getLogs(): Call<LogResp>

    @POST("purgeLogs")
    fun purgeLogs(): Call<LogResp>
}

fun <T> apiCall(
    call: Call<T>?,
    onSuccess: (Response<T>) -> Unit,
    onError: (Throwable) -> Unit
) {
    if (call == null) {
        onError(APICallException("Call object is null, Retrofit client probably not initialised"))
        return
    }

    call.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                onSuccess(response)
            } else {
                onError(APICallException("${response.message().ifBlank { response.raw() }}"))
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onError(t)
        }
    })
}

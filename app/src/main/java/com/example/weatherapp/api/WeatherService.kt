package com.example.weatherapp.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import coil.ImageLoader
import coil.request.ImageRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherService (private val context : Context) {
    private var weatherAPI: WeatherServiceAPI
    private val searches = mutableListOf<Call<List<APILocation>?>>()
    private val imageLoader = ImageLoader.Builder(context).allowHardware(false).build()

    init {
        val retrofitAPI = Retrofit.Builder().baseUrl(WeatherServiceAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        weatherAPI = retrofitAPI.create(WeatherServiceAPI::class.java)
    }

    fun getName(lat: Double, lng: Double, onResponse: (String?) -> Unit) {
        search("$lat,$lng") { list ->
            list?.forEach { onResponse(it.name) }
        }
    }

    fun getLocation(name: String, onResponse: (lat: Double?, long: Double?) -> Unit) {
        // TODO – Adaptar getName(); lembre que o lambda recebe dois parametros
        search(name) { list ->
            list?.forEach { onResponse(it.lat, it.lon) }
        }
    }

    fun search(query: String, onResponse: (List<APILocation>?) -> Unit) {
        val call: Call<List<APILocation>?> = weatherAPI.search(query)
        searches.add(call)
        call.enqueue(object : Callback<List<APILocation>?> {
            override fun onResponse(
                call: Call<List<APILocation>?>,
                response: Response<List<APILocation>?>
            ) {
                onResponse(response.body())
                searches.remove(call)
            }

            override fun onFailure(call: Call<List<APILocation>?>, t: Throwable) {
                Log.w("WeatherApp WARNING", "" + t.message)
                onResponse(null)
                searches.remove(call)
            }
        })
    }

    fun cancelAll() {
        searches.forEach { it.cancel() }

    }

    private fun <T> enqueue(call : Call<T?>, onResponse : ((T?) -> Unit)? = null){
        call.enqueue(object : Callback<T?> {
            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                val obj: T? = response.body()
                onResponse?.invoke(obj)
            }
            override fun onFailure(call: Call<T?>, t: Throwable) {
                Log.w("WeatherApp WARNING", "" + t.message)
            }
        })
    }

    fun getWeather(name: String, onResponse: (APICurrentWeather?) -> Unit){
        val call: Call<APICurrentWeather?> = weatherAPI.weather(name)
        enqueue(call) { onResponse.invoke(it) }
    }

    fun getForecast(name: String, onResponse : (APIWeatherForecast?) -> Unit) {
        val call: Call<APIWeatherForecast?> = weatherAPI.forecast(name)
        enqueue(call) { onResponse.invoke(it) }
    }

    fun getBitmap(imgUrl: String, onResponse: (Bitmap?) -> Unit) {
        val request = ImageRequest.Builder(context)
            .data(imgUrl).allowHardware(false).target(
                onSuccess = { drawable ->
                    val bitmap = (drawable as BitmapDrawable).bitmap
                    onResponse(bitmap)
                },
                onError = { /* handle failure */ }
            )
            .build()
        imageLoader.enqueue(request)
    }
}
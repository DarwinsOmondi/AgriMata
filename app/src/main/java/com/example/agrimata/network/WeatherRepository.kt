package com.example.agrimata.network

import com.example.agrimata.model.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(WeatherApi::class.java)

    suspend fun fetchWeather(latitude: Double, longitude: Double): WeatherResponse {
        return api.getWeather(latitude, longitude)
    }
}

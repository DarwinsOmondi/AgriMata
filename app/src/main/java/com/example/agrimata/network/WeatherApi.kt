package com.example.agrimata.network

import com.example.agrimata.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m,rain,wind_speed_10m,cloud_cover,weather_code",
        @Query("models") models: String = "icon_seamless"
    ): WeatherResponse
}

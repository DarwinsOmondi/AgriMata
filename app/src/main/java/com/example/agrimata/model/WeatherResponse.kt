package com.example.agrimata.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("hourly") val hourly: HourlyData
)

data class HourlyData(
    @SerializedName("temperature_2m") val temperatures: List<Double>,
    @SerializedName("rain") val rain: List<Double>,
    @SerializedName("wind_speed_10m") val windSpeed: List<Double>,
    @SerializedName("cloud_cover") val cloudCover: List<Double>
)
package com.example.agrimata.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.WeatherResponse
import com.example.agrimata.network.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    var weatherState = mutableStateOf<WeatherResponse?>(null)
    var errorMessage = mutableStateOf<String?>(null)


    fun fetchWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val response = WeatherRepository.fetchWeather(latitude, longitude)
                weatherState.value = response
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }
}
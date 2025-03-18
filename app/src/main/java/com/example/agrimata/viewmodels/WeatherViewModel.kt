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

    init {
        fetchWeather()
    }

    fun fetchWeather() {
        viewModelScope.launch {
            try {
                val response = WeatherRepository.fetchWeather(-1.2833, 36.8167) // Nairobi
                weatherState.value = response
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }
}
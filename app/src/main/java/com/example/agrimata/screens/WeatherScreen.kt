package com.example.agrimata.screens

import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agrimata.viewmodels.PermissionViewModel
import com.example.agrimata.viewmodels.WeatherViewModel

@Composable
fun WeatherScreen(onBack:() -> Unit) {
    val context = LocalContext.current
    val weatherViewModel: WeatherViewModel = viewModel()
    val locationViewModel: PermissionViewModel = viewModel()

    val locationPermissionGranted by remember { mutableStateOf(locationViewModel.checkUserLocationPermission(context)) }
    var userLocation by remember { mutableStateOf<Location?>(null) }
    val weatherState by weatherViewModel.weatherState
    val errorMessage by weatherViewModel.errorMessage


    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.secondary

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted) {
            locationViewModel.getUserLocation(context) { location ->
                if (location != null) {
                    userLocation = location
                    Log.d("WeatherScreen", "Location: ${location.latitude}, ${location.longitude}")
                    weatherViewModel.fetchWeather(location.latitude, location.longitude)
                } else {
                    Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            locationViewModel.requestUserLocationPermission(context)
        }
    }
    Scaffold(Modifier.fillMaxSize()){ innerPadding ->
        Column(Modifier.padding(innerPadding)){
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = {
                    onBack()
                },
                Modifier.align(Alignment.Start)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Back", tint = textColor)
            }
            Spacer(Modifier.weight(.5f))
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                when {
                    userLocation == null -> {
                        Text(text = "Fetching location...")
                    }
                    errorMessage != null -> {
                        Text(text = "Error: ${errorMessage}")
                    }
                    weatherState == null -> {
                        Text(text = "Fetching weather...")
                    }
                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Weather: ${weatherState?.hourly?.temperatures?.get(0)}°C",color = textColor)
                            Text(text = "Rain: ${weatherState?.hourly?.rain?.get(0)} mm",color = textColor)
                            Text(text = "Wind Speed: ${weatherState?.hourly?.windSpeed?.get(0)} m/s",color = textColor)
                            Text(text = "Cloud Cover: ${weatherState?.hourly?.cloudCover?.get(0)}%",color = textColor)
                        }
                    }
                }
            }
        }
    }

}

//Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//
//    when {
//        userLocation == null -> {
//            Text(text = "Fetching location...")
//        }
//        errorMessage != null -> {
//            Text(text = "Error: ${errorMessage}")
//        }
//        weatherState == null -> {
//            Text(text = "Fetching weather...")
//        }
//        else -> {
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Text(text = "Weather: ${weatherState?.hourly?.temperatures?.get(0)}°C",color = textColor)
//                Text(text = "Rain: ${weatherState?.hourly?.rain?.get(0)} mm",color = textColor)
//                Text(text = "Wind Speed: ${weatherState?.hourly?.windSpeed?.get(0)} m/s",color = textColor)
//                Text(text = "Cloud Cover: ${weatherState?.hourly?.cloudCover?.get(0)}%",color = textColor)
//            }
//        }
//    }
//}

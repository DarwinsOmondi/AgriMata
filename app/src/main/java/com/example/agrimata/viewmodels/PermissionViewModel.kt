package com.example.agrimata.viewmodels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class PermissionViewModel: ViewModel() {

    companion object {
        private const val LOCATION_PERMISSION_CODE = 1001
        private const val CAMERA_PERMISSION_CODE = 1002
    }

    fun checkUserLocationPermission(context: Context): Boolean{
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestUserLocationPermission(context: Context){
        if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            Toast.makeText(context,"Location permission is required for this app", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        }
    }
    fun checkCameraAndStoragePermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraAndStoragePermissions(context: Context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.CAMERA) ||
            ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_EXTERNAL_STORAGE) ||
            ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
            ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.RECORD_AUDIO)
        ) {
            Toast.makeText(context, "Camera, storage, and audio permissions are required", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
                ),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation(context: Context, onLocationReceived: (Location?) -> Unit) {
        if (!checkUserLocationPermission(context)) {
            requestUserLocationPermission(context)
            return
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationProvider = LocationManager.GPS_PROVIDER

        if (locationManager.isProviderEnabled(locationProvider)) {
            val location = locationManager.getLastKnownLocation(locationProvider)
            viewModelScope.launch {
                delay(2000)
                onLocationReceived(location)
            }
        } else {
            Toast.makeText(context, "Enable GPS for location access", Toast.LENGTH_SHORT).show()
            onLocationReceived(null)
        }
    }

    fun decodeLocation(context: Context, location: Location?): String {
        if (location == null) return "Location not available"

        val latitude = location.latitude
        val longitude = location.longitude
        var addressText = "Lat: $latitude, Lng: $longitude"

        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                addressText = address.getAddressLine(0) ?: addressText
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return addressText
    }
}
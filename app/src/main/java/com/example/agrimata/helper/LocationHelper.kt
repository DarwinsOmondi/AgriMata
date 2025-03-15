package com.example.agrimata.helper

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getUserLocation(onLocationReceived: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                onLocationReceived(location)
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
            onLocationReceived(null)
        }
    }

    fun getLocationName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "Unknown Location"
            }
        } catch (e: IOException) {
            "Location not found"
        }
    }
}
package com.example.agrimata.viewmodels

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel

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
}
package co.swatisi.team.metrowalkdc

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import com.google.android.gms.location.LocationRequest
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient

/**
 * Created by ihasanov on 9/19/17.
 */

private const val TAG = "LocationDetector"

class LocationDetector {

    // NOTES: request location updates, timeout after 10 seconds and fall back on last known location,
    // if it exists…..otherwise fail

    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    fun startLocationUpdates(context: Context) {
        // Create the location request and set properties
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000 // 10 secs
        locationRequest.fastestInterval = 3000 // 3 secs

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        // Check if the current location settings are satisfied
        val settingsClient = LocationServices.getSettingsClient(context)
        settingsClient.checkLocationSettings(locationSettingsRequest)
    }

    fun getLastLocation(context: Activity) {

        // Runtime permissions check
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }

        // TODO: define accept and reject scenarios for runtime permission check

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(OnSuccessListener<Any> { location ->
                    // GPS location can be null if GPS is switched off
                    if (location != null) {
                        onLocationChanged(location)
                    }
                })
                .addOnFailureListener(OnFailureListener { e ->
                    Log.d(TAG, "Error trying to get last GPS location")
                    e.printStackTrace()
                })
    }

    private fun onLocationChanged(location: Any?) {}
}


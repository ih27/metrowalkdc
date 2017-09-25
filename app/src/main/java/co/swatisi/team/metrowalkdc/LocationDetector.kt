package co.swatisi.team.metrowalkdc

import android.Manifest
import android.app.Activity
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.Result
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task


/**
 * Created by ihasanov on 9/19/17.
 */

private const val TAG = "LocationDetector"

object LocationDetector {

    // NOTES: request location updates, timeout after 10 seconds and fall back on last known location,
    // if it exists…..otherwise fail

    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null

    fun startLocationUpdates(context: Activity, fusedLocationClient: FusedLocationProviderClient?) {
        // Create the location request and set properties
        locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 secs
            fastestInterval = 3000 // 3 secs
        }


        // Create LocationSettingsRequest object using location request
        // LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()

        // Check if the current location settings are satisfied
//        val settingsClient = LocationServices.getSettingsClient(context)
//        settingsClient.checkLocationSettings(locationSettingsRequest)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val location = locationResult?.locations?.find { location -> location != null }
                location?.let {
                    lastLocation = location
                    Log.d(TAG, location.toString())
                }
            }
        }

        // Runtime permissions check
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // TODO: define accept and reject scenarios for runtime permission check

        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
        val locationResult = fusedLocationClient?.lastLocation
        locationResult?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                lastLocation = locationResult.result
            } else {
                Log.d(TAG, "Current location is null.")
                Log.e(TAG, "Exception: %s", task.exception)
            }
        }
    }

    fun stopLocationUpdates(fusedLocationClient: FusedLocationProviderClient?) {

        fusedLocationClient?.removeLocationUpdates(locationCallback);
    }

    fun getLastLocation(): Location? {
        return lastLocation
    }
}


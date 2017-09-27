package co.swatisi.team.metrowalkdc.utility

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class LocationDetector(val context: Context, val fusedLocationClient: FusedLocationProviderClient?) {
    private val TAG = "LocationDetector"
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null

    var locationCompletionListener: LocationCompletionListener? = null

    interface LocationCompletionListener {
        fun locationKnown()
        fun locationUnknown()
    }

    fun startLocationUpdates() {
        // Create the location request and set properties
        locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 secs
            fastestInterval = 3000 // 3 secs
        }

        // Define location callback object
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val location = locationResult?.locations?.find { location -> location != null }
                location?.let {
                    lastLocation = location
                }
            }
        }

        // Try to get the location updates
        try {
            fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, null)
            val locationResult = fusedLocationClient?.lastLocation
            locationResult?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lastLocation = locationResult.result

                    Log.d(TAG, "Task success: ${locationResult.toString()}")
                    // Success - Location available
                    locationCompletionListener?.locationKnown()
                } else {
                    // Fail - No location available
                    locationCompletionListener?.locationUnknown()
                    Log.e(TAG, "Task Exception: %${task.exception}")
                }
            }
        } catch (e: SecurityException) {
            // Fail - No location available
            locationCompletionListener?.locationUnknown()
            Log.e(TAG,"Security Exception: ${e.cause}")
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    fun getLastLocation(): Location? {
        return lastLocation
    }
}


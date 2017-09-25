package co.swatisi.team.metrowalkdc

import android.location.Location
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener



private const val TAG = "LocationDetector"

object LocationDetector {

    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null

    fun startLocationUpdates(fusedLocationClient: FusedLocationProviderClient?) {
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
                    Log.d(TAG, location.toString())
                }
            }
        }

        // Try to get the location updates after getting last location
        try {
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                if (location != null) {
                    lastLocation = location
                }
            }

            fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, null)
            val locationResult = fusedLocationClient?.lastLocation
            locationResult?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lastLocation = locationResult.result
                } else {
                    Log.d(TAG, "Last location is null.")
                    Log.e(TAG, "Task Exception: %s", task.exception)
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG,"Security Exception: %s", e.cause)
        }
    }

    fun stopLocationUpdates(fusedLocationClient: FusedLocationProviderClient?) {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    fun getLastLocation(): Location? {
        return lastLocation
    }
}


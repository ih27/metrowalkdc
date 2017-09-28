package co.swatisi.team.metrowalkdc.utility

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class LocationDetector(val context: Context, private val fusedLocationClient: FusedLocationProviderClient?) {
    private val tag = "LocationDetector"
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
                    // Success - Location available
                    locationCompletionListener?.locationKnown()
                } else {
                    // Fail - No location available
                    locationCompletionListener?.locationUnknown()
                    Log.e(tag, "Task Exception: %${task.exception}")
                }
            }
        } catch (e: SecurityException) {
            // Fail - No location available
            locationCompletionListener?.locationUnknown()
            Log.e(tag,"Security Exception: ${e.cause}")
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    fun getLastLocation(): Location? {
        return lastLocation
    }

    // Check if the location services are turned off
    fun isLocationServiceEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            Log.e(tag, "Couldn't determine GPS status")
        }

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            Log.e(tag, "Couldn't determine Network location status")
        }

        return gpsEnabled || networkEnabled
    }
}


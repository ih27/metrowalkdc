package co.swatisi.team.metrowalkdc.utility

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import co.swatisi.team.metrowalkdc.R
import com.google.android.gms.location.*
import java.util.*
import kotlin.concurrent.timerTask

class LocationTracker(val context: Context) {
    private val tag = "LocationTracker"
    private val fusedLocationClient = FusedLocationProviderClient(context)

    init {
        // Send to location settings page in case the location services are turned off
        if (!isLocationServiceEnabled()) {
            Toast.makeText(context, context.getString(R.string.location_detector_enable_services_message),
                    Toast.LENGTH_LONG).show()
            val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(locationSettingsIntent)
        }
    }

    // Reasons location tracking might fail
    enum class FailureReason {
        TIMEOUT,
        NO_PERMISSION
    }

    var locationListener: LocationListener? = null

    interface LocationListener {
        fun locationFound(location: Location)
        fun locationNotFound(reason: FailureReason)
    }

    fun getLocation() {
        // Create the location request and set properties
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
        }

        // Check if location permission granted
        val permissionResult = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionResult == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Create timer
            val timer = Timer()

            //create location detection callback
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    // Stop location updates
                    fusedLocationClient.removeLocationUpdates(this)

                    // Cancel timer
                    timer.cancel()

                    // Fire callback with location
                    locationListener?.locationFound(locationResult.locations.first())
                }
            }

            // Timer scheduled
            timer.schedule(timerTask {
                // Stop location updates and fire callback
                fusedLocationClient.removeLocationUpdates(locationCallback)
                locationListener?.locationNotFound(FailureReason.TIMEOUT)
            }, 10*1000) // 10 seconds


            // Start location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
        else {
            // Location permission is not granted
            locationListener?.locationNotFound(FailureReason.NO_PERMISSION)
        }
    }

    // Check if the location services are enabled
    private fun isLocationServiceEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            Log.e(tag, context.getString(R.string.location_detector_gps_error))
        }

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            Log.e(tag, context.getString(R.string.location_detector_network_error))
        }

        return gpsEnabled || networkEnabled
    }
}

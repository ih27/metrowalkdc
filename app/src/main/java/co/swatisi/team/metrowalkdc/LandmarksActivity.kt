package co.swatisi.team.metrowalkdc

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

private const val TAG = "LandmarksActivity"
private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

class LandmarksActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var selectedLocation: Location? = null
    private var requestingLocationUpdates = true
    private var locationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

        // Get the required runtime location permission
        getLocationPermission()

        if (locationPermissionGranted) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            LocationDetector.startLocationUpdates(fusedLocationClient)
            requestingLocationUpdates = true

            // TODO: Show ProgressDialog and then get the last location

            selectedLocation = LocationDetector.getLastLocation()

            Log.d(TAG, selectedLocation.toString())

            FetchLandmarksAsyncTask.getLandmarksList(this, selectedLocation)
        } else {
            Log.d(TAG, "The permission is not granted.")
        }
    }

    override fun onResume() {
        super.onResume()

        // Start the location updates
        if (!requestingLocationUpdates) {
            LocationDetector.startLocationUpdates(fusedLocationClient)
            requestingLocationUpdates = true
        }
    }

    override fun onPause() {
        super.onPause()

        // Stop the location updates to save power consumption
        if (requestingLocationUpdates) {
            LocationDetector.stopLocationUpdates(fusedLocationClient)
            requestingLocationUpdates = false
        }
    }

    private fun getLocationPermission() {
        /*  The result of the runtime permission request is handled by a callback,
            onRequestPermissionsResult */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    // Location runtime permission flag is adjusted based on the user action
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // Kotlin short if statement
            locationPermissionGranted = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }
}
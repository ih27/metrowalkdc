package co.swatisi.team.metrowalkdc

import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_landmarks.*

private const val TAG = "LandmarksActivity"

class LandmarksActivity : AppCompatActivity() {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var requestingLocationUpdates = true
    private var selectedLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        LocationDetector.startLocationUpdates(this, fusedLocationClient)
        requestingLocationUpdates = true

        // TODO: Show ProgressDialog and then get the last location

        selectedLocation = LocationDetector.getLastLocation()

        Log.d(TAG, selectedLocation.toString())

        FetchLandmarksAsyncTask.getLandmarksList(this, selectedLocation)
    }

    override fun onResume() {
        super.onResume()

        // Start the location updates
        if (!requestingLocationUpdates) {
            LocationDetector.startLocationUpdates(this, fusedLocationClient)
            requestingLocationUpdates = true
            selectedLocation = LocationDetector.getLastLocation()

            Log.d(TAG, selectedLocation.toString())

            FetchLandmarksAsyncTask.getLandmarksList(this, selectedLocation)
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
}

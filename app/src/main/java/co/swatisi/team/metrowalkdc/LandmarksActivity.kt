package co.swatisi.team.metrowalkdc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_landmarks.*

private const val TAG = "LandmarksActivity"

class LandmarksActivity : AppCompatActivity() {

    private val locationDetector = LocationDetector()
    private var requestingLocationUpdates = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)
    }

    override fun onResume() {
        super.onResume()

        // Start the location updates
        if (!requestingLocationUpdates) {
            locationDetector.startLocationUpdates(this)
            requestingLocationUpdates = true
        }
    }

    override fun onPause() {
        super.onPause()

        // Stop the location updates to save power consumption
        if (requestingLocationUpdates) {
            locationDetector.stopLocationUpdates()
            requestingLocationUpdates = false
        }
    }
}

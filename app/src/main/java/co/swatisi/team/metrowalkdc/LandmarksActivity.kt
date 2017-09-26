package co.swatisi.team.metrowalkdc

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import co.swatisi.team.metrowalkdc.model.LandmarkData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_landmarks.*
import org.jetbrains.anko.activityUiThread
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

class LandmarksActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback,
                            LocationDetector.LocationCompletionListener {

    private val TAG = "LandmarksActivity"
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var selectedLocation: Location? = null
    private var requestingLocationUpdates = true
    private var locationPermissionGranted = false

    private lateinit var locationDetector: LocationDetector
    private lateinit var fetchLandmarksManager: FetchLandmarksManager
    private lateinit var staggeredLayoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: LandmarksAdapter

    private var progressDialog: ProgressDialog? = null

    private val onItemClickListener = object : LandmarksAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

        // Get the required runtime location permission
        getLocationPermission()

        if (locationPermissionGranted) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationDetector = LocationDetector(this,fusedLocationClient)
            locationDetector.locationCompletionListener = this

            locationDetector.startLocationUpdates()
            requestingLocationUpdates = true

            // Show ProgressDialog
            progressDialog = ProgressDialog(this)
            progressDialog?.setCancelable(false)
            progressDialog?.setMessage("Loading")
            progressDialog?.isIndeterminate=true
            progressDialog?.show()

        } else {
            Log.d(TAG, "The permission is not granted.")
        }
    }

    override fun onResume() {
        super.onResume()

        // Start the location updates
        if (!requestingLocationUpdates) {
            locationDetector.startLocationUpdates()
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

    override fun locationKnown() {
        selectedLocation = locationDetector.getLastLocation()
        Log.d(TAG, selectedLocation.toString())
        fetchLandmarksManager = FetchLandmarksManager(this, selectedLocation)

        // Show progressbar while getting the landmarks
        doAsync {
            val landmarks = fetchLandmarksManager.getLandmarksList()
            if (landmarks != null) {
                activityUiThread {
                    // Check if we have the landmark data
                    if(LandmarkData.landmarkList().isEmpty()) {
                        LandmarkData.updateList(landmarks)
                    }

                    // Hide the ProgressDialog
                    progressDialog?.hide()

                    staggeredLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    landmarksList.layoutManager = staggeredLayoutManager

                    adapter = LandmarksAdapter()
                    landmarksList.adapter = adapter
                    adapter.setOnItemClickListener(onItemClickListener)
                }
            } else {
                Log.e(TAG, "Something wrong with fetching landmarks")
            }
        }
    }

    override fun locationUnknown() {
        toast("Location is unknown")
    }
}
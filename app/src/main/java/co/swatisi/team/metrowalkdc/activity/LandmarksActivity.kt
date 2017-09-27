package co.swatisi.team.metrowalkdc.activity

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
import co.swatisi.team.metrowalkdc.utility.FetchLandmarksManager
import co.swatisi.team.metrowalkdc.adapter.LandmarksAdapter
import co.swatisi.team.metrowalkdc.utility.LocationDetector
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.LandmarkData
import co.swatisi.team.metrowalkdc.model.StationData
import co.swatisi.team.metrowalkdc.utility.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_landmarks.*
import org.jetbrains.anko.activityUiThread
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

class LandmarksActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback,
        LocationDetector.LocationCompletionListener {

    private val tag = "LandmarksActivity"
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var selectedLocation: Location? = null
    private var requestingLocationUpdates = false
    private var locationPermissionGranted = false

    private var locationDetector: LocationDetector? = null
    private lateinit var fetchLandmarksManager: FetchLandmarksManager
    private lateinit var staggeredLayoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: LandmarksAdapter
    private var stationList = StationData.stationList()

    private var progressDialog: ProgressDialog? = null

    private val onItemClickListener = object : LandmarksAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

        // Check if coming from MetroStationsActivity
        if(intent.hasExtra("latitude") || intent.hasExtra("longitude")) {
            val metroLat = intent.getDoubleExtra("latitude", 0.0)
            val metroLon = intent.getDoubleExtra("longitude", 0.0)
            getLandmarksAndShow(metroLat, metroLon)
        } else {
            // Closest station needed, so location permission check initiated
            getLocationPermission()
        }
    }

    override fun onResume() {
        super.onResume()

        // Start the location updates
        if (!requestingLocationUpdates && locationDetector != null) {
            locationDetector?.startLocationUpdates()
            requestingLocationUpdates = true
        }
    }

    override fun onPause() {
        super.onPause()

        // Stop the location updates to save power consumption
        if (requestingLocationUpdates && locationDetector != null) {
            locationDetector?.stopLocationUpdates()
            requestingLocationUpdates = false
        }
    }

    // Request location permission, if not enabled
    private fun getLocationPermission() {
        /*  The result of the runtime permission request is handled by a callback,
            onRequestPermissionsResult */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, set up the activity
            locationPermissionGranted = true
            setUp()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    // Location runtime permission flag is adjusted based on the user action
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // Kotlin short if statement
            locationPermissionGranted = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED

            // Based on the user selection either set up the activity
            // or finish it going back to the menu
            if (locationPermissionGranted) setUp() else finish()
        }
    }
    // Set up the activity's components
    private fun setUp() {
        // Start the location updates
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationDetector = LocationDetector(this, fusedLocationClient)
        locationDetector?.locationCompletionListener = this

        locationDetector?.startLocationUpdates()
        requestingLocationUpdates = true

        // LocationListener interface callbacks will determine the flow
        // of the activity next
    }

    private fun showProgressWhileWaiting() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setCancelable(false)
        progressDialog?.setMessage("Loading")
        progressDialog?.isIndeterminate=true
        progressDialog?.show()
    }

    private fun getClosestStationCoordinates(): Pair<out Double, out Double> {
        // Iterate over the metro stations list to find the closest
        var minDistance = Double.MAX_VALUE
        var minIndex = 0
        stationList.forEachWithIndex { index, station ->
            val stationLocation = Location("")
            stationLocation.latitude = station.lat
            stationLocation.longitude = station.lon
            val distance = stationLocation.distanceTo(selectedLocation)
            if (distance < minDistance) {
                minDistance = distance.toDouble()
                minIndex = index
            }
        }
        Log.d(tag, "The closest station is: ${stationList[minIndex].name}")
        return Pair(stationList[minIndex].lat, stationList[minIndex].lon)
    }

    private fun populateRecyclerView() {
        staggeredLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        landmarksList.layoutManager = staggeredLayoutManager

        adapter = LandmarksAdapter()
        landmarksList.adapter = adapter
        adapter.setOnItemClickListener(onItemClickListener)
    }

    private fun getLandmarksAndShow(lat: Double, lon: Double) {
        // Show ProgressDialog
        showProgressWhileWaiting()

        fetchLandmarksManager = FetchLandmarksManager(this@LandmarksActivity, lat, lon)

        doAsync {
            val landmarks = fetchLandmarksManager.getLandmarksList()
            Log.d(tag, "Landmarks: ${landmarks.toString()}")
            if (landmarks != null) {
                activityUiThread {
                    // Check if we have the landmark data
                    if(LandmarkData.landmarkList().isEmpty()) {
                        LandmarkData.updateList(landmarks)
                    }

                    // Hide the ProgressDialog
                    progressDialog?.dismiss()

                    // Show the list
                    populateRecyclerView()
                }
            } else {
                Log.e(tag, "Something wrong with fetching landmarks")
            }
        }
    }

    override fun locationKnown() {
        selectedLocation = locationDetector?.getLastLocation()
        selectedLocation?.let {
            val (closestLat, closestLon) = getClosestStationCoordinates()
            getLandmarksAndShow(closestLat, closestLon)
        }
    }

    override fun locationUnknown() {
        toast("Location is unknown")
    }
}
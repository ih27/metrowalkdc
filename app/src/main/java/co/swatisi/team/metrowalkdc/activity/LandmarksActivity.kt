package co.swatisi.team.metrowalkdc.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import co.swatisi.team.metrowalkdc.adapter.LandmarksAdapter
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.Landmark
import co.swatisi.team.metrowalkdc.model.StationData
import co.swatisi.team.metrowalkdc.utility.*
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_landmarks.*
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.*

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

    private lateinit var persistenceManager: PersistenceManager
    private var recyclerViewList: List<Landmark> = listOf()
    private var functionality = ""

    private val onItemClickListener = object : LandmarksAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            val intent = Intent(this@LandmarksActivity, LandmarkDetailActivity::class.java)
            val landmark = recyclerViewList[position]
            intent.putExtra("landmark", landmark)
            // Start the LandmarkDetailActivity with the selected landmark passed
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore the label
            supportActionBar?.title = savedInstanceState.getString(Constants.LANDMARKS_LABEL_KEY)
            // Restore value of list and populate the view
            recyclerViewList = savedInstanceState.getParcelableArrayList(Constants.LANDMARKS_LIST_KEY)
            populateRecyclerView(recyclerViewList)
            // Hide the ProgressBar
            landmarks_progress_bar.visibility = View.GONE
        } else {
            // Check where the user is coming from
            if(intent.hasExtra("latitude") || intent.hasExtra("longitude")) {
                functionality = getString(R.string.landmark_functionality_metro)
                supportActionBar?.title = getString(R.string.metro_station_landmarks_activity_label)
                val metroLat = intent.getDoubleExtra("latitude", 0.0)
                val metroLon = intent.getDoubleExtra("longitude", 0.0)
                getLandmarksAndShow(metroLat, metroLon)
            } else if(intent.hasExtra("favorites")) {
                functionality = getString(R.string.landmark_functionality_favorites)
                supportActionBar?.title = getString(R.string.favorites_landmarks_activity_label)
                // Get the persistence manager for favorites functionality
                persistenceManager = PersistenceManager(this)
                getFavoritesAndShow()
            } else {
                functionality = getString(R.string.landmark_functionality_closest)
                supportActionBar?.title = getString(R.string.closest_station_landmarks_activity_label)
                // Closest station needed, so location permission check initiated
                getLocationPermission()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Start the location updates
        if (!requestingLocationUpdates && locationDetector != null) {
            locationDetector?.startLocationUpdates()
            requestingLocationUpdates = true
        }

        // If it is the favorites list, repopulate the view in case a favorite is deleted
        if (functionality == getString(R.string.landmark_functionality_favorites)) {
            getFavoritesAndShow()
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

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelableArrayList(Constants.LANDMARKS_LIST_KEY,
                ArrayList<Landmark>(recyclerViewList))
        outState?.putString(Constants.LANDMARKS_LABEL_KEY, supportActionBar?.title.toString())
        super.onSaveInstanceState(outState)
    }

    private fun populateRecyclerView(list: List<Landmark>) {
        staggeredLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        landmarksList.layoutManager = staggeredLayoutManager

        adapter = LandmarksAdapter(this, list)
        landmarksList.adapter = adapter
        adapter.setOnItemClickListener(onItemClickListener)
    }

    private fun getLandmarksAndShow(lat: Double, lon: Double) {
        fetchLandmarksManager = FetchLandmarksManager(this@LandmarksActivity, lat, lon)

        doAsync {
            recyclerViewList = fetchLandmarksManager.fetchLandmarks()
            activityUiThread {
                if (recyclerViewList.isNotEmpty()) {
                    populateRecyclerView(recyclerViewList)
                    // Hide the ProgressBar
                    landmarks_progress_bar.visibility = View.GONE
                } else {
                    toast("No landmarks to show.")
                    finish()
                }
            }
        }
    }

    private fun getFavoritesAndShow() {
        doAsync {
            recyclerViewList = persistenceManager.fetchFavorites()
            activityUiThread {
                if (recyclerViewList.isNotEmpty()) {
                    populateRecyclerView(recyclerViewList)
                    // Hide the ProgressBar
                    landmarks_progress_bar.visibility = View.GONE
                } else {
                    toast("No favorites to show.")
                    finish()
                }
            }
        }
    }

    // Get the coordinates for the closest metro station
    private fun getClosestStationCoordinates(): Pair<Double, Double> {
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
        return Pair(stationList[minIndex].lat, stationList[minIndex].lon)
    }

    /*
     *  Location Permission Code
     */
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationDetector = LocationDetector(this, fusedLocationClient)
        locationDetector?.locationCompletionListener = this

        // Start the location updates
        locationDetector?.startLocationUpdates()
        requestingLocationUpdates = true

        // LocationListener interface callbacks will determine the flow
        // of the activity
    }

    // Run if the location is known
    override fun locationKnown() {
        selectedLocation = locationDetector?.getLastLocation()

        // First, check if the metro station list is available
        if (stationList.isEmpty()) {
            // Get the metro stations
            FetchMetroStationsManager.getStationList(this)
        }

        if (selectedLocation != null) {
            val (closestLat, closestLon) = getClosestStationCoordinates()
            getLandmarksAndShow(closestLat, closestLon)
        } else {
            toast("We still have no location.")
            finish()
        }
    }

    override fun locationUnknown() {
        toast("Location is unknown")
    }
}
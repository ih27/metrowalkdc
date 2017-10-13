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
import android.widget.Toast
import co.swatisi.team.metrowalkdc.adapter.LandmarksAdapter
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.Landmark
import co.swatisi.team.metrowalkdc.model.Station
import co.swatisi.team.metrowalkdc.utility.*
import kotlinx.android.synthetic.main.activity_landmarks.*
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.*

class LandmarksActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback,
        LocationDetector.LocationListener {

    private var selectedLocation: Location? = null
    private var locationPermissionGranted = false
    private var locationDetector: LocationDetector? = null
    private var recyclerViewList: List<Landmark> = listOf()
    private var functionality = ""

    private lateinit var fetchLandmarksManager: FetchLandmarksManager
    private lateinit var fetchMetroStationsManager: FetchMetroStationsManager
    private lateinit var persistenceManager: PersistenceManager
    private lateinit var staggeredLayoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: LandmarksAdapter
    private lateinit var stationList: List<Station>

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
            // Restore the label and functionality string
            supportActionBar?.title = savedInstanceState.getString(Constants.LANDMARKS_LABEL_KEY)
            functionality = savedInstanceState.getString(Constants.FUNCTIONALITY_KEY)

            // Restore value of list and populate the view
            recyclerViewList = savedInstanceState.getParcelableArrayList(Constants.LIST_KEY)
            populateRecyclerView(recyclerViewList)

            // Hide the ProgressBar
            landmarks_progress_bar.visibility = View.GONE
        } else {
            // Check where the user is coming from and call the corresponding function
            if(intent.hasExtra("latitude") || intent.hasExtra("longitude")) {
                functionality = getString(R.string.landmark_functionality_metro)
                supportActionBar?.title = getString(R.string.metro_station_landmarks_activity_label)
                val metroLat = intent.getDoubleExtra("latitude", 0.0)
                val metroLon = intent.getDoubleExtra("longitude", 0.0)
                getLandmarksAndShow(metroLat, metroLon)
            } else if(intent.hasExtra("favorites")) {
                functionality = getString(R.string.landmark_functionality_favorites)
                supportActionBar?.title = getString(R.string.favorites_landmarks_activity_label)
                getFavoritesAndShow()
            } else {
                functionality = getString(R.string.landmark_functionality_closest)
                supportActionBar?.title = getString(R.string.closest_station_landmarks_activity_label)

                doAsync {
                    fetchMetroStationsManager = FetchMetroStationsManager(this@LandmarksActivity)
                    stationList = fetchMetroStationsManager.fetchStations()

                    activityUiThread {
                        // Closest station needed, so location permission check initiated
                        requestLocationPermission()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // If it is the favorites list, repopulate the view in case a favorite is deleted
        if (functionality == getString(R.string.landmark_functionality_favorites)) {
            getFavoritesAndShow()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelableArrayList(Constants.LIST_KEY, ArrayList<Landmark>(recyclerViewList))
        outState?.putString(Constants.LANDMARKS_LABEL_KEY, supportActionBar?.title.toString())
        outState?.putString(Constants.FUNCTIONALITY_KEY, functionality)
        super.onSaveInstanceState(outState)
    }

    // Set up the Recycler View
    private fun populateRecyclerView(list: List<Landmark>) {
        staggeredLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        landmarksList.layoutManager = staggeredLayoutManager

        adapter = LandmarksAdapter(this, list)
        landmarksList.adapter = adapter
        adapter.setOnItemClickListener(onItemClickListener)
    }

    // Get the landmarks list and populate the view
    private fun getLandmarksAndShow(lat: Double, lon: Double) {
        // Get the fetch landmarks manager
        fetchLandmarksManager = FetchLandmarksManager(this@LandmarksActivity, lat, lon)

        doAsync {
            recyclerViewList = fetchLandmarksManager.fetchLandmarks()
            activityUiThread {
                if (recyclerViewList.isNotEmpty()) {
                    populateRecyclerView(recyclerViewList)
                    // Hide the ProgressBar
                    landmarks_progress_bar.visibility = View.GONE
                } else {
                    val distance = (Constants.RADIUS.toInt() * Constants.MILES_CONVERSION).toInt().toString()
                            toast("No landmarks within $distance miles.")
                    finish()
                }
            }
        }
    }

    // Get the favorites list and populate the view
    private fun getFavoritesAndShow() {
        // Get the persistence manager for favorites functionality
        persistenceManager = PersistenceManager(this)

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

    // Utility function to get the coordinates for the closest metro station
    private fun getClosestStationCoordinates(): Pair<Double, Double>? {
        // Metro stations list is empty
        if (stationList.isEmpty()) return null
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

    // Set up the activity's components
    private fun setUp() {
        locationDetector = LocationDetector(this)
        locationDetector?.locationListener = this

        // Start the location detector
        locationDetector?.getLocation()
    }

    // Fires if the location is found
    override fun locationFound(location: Location) {
        //selectedLocation = locationDetector?.getLastLocation()
        selectedLocation = location

        if (selectedLocation != null) {
            // Get the closest metro station coordinates
            val closestLat = getClosestStationCoordinates()?.component1()
            val closestLon = getClosestStationCoordinates()?.component2()
            if (closestLat != null && closestLon != null) {
                getLandmarksAndShow(closestLat, closestLon)
            }
            else {
                toast(getString(R.string.metro_stations_list_empty_error))
                finish()
            }
        } else {
            toast(getString(R.string.null_location_error))
            finish()
        }
    }

    // Fires if there is an issue with the location
    override fun locationNotFound(reason: LocationDetector.FailureReason) = when (reason) {
        LocationDetector.FailureReason.TIMEOUT -> {
            runOnUiThread {
                Toast.makeText(this, getString(R.string.location_detector_timeout_error),
                        Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        LocationDetector.FailureReason.NO_PERMISSION -> {
            Toast.makeText(this, getString(R.string.location_detector_no_permission_error),
                    Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /*
 *  Location Permission Code
 */
    // Request location permission, if not enabled
    private fun requestLocationPermission() {
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
                    grantResults.first() == PackageManager.PERMISSION_GRANTED

            // Based on the user selection either set up the activity
            // or finish it going back to the menu
            if (locationPermissionGranted) {
                setUp()
            } else {
                toast(getString(R.string.location_detector_no_permission_error))
                finish()
            }
        }
    }
}
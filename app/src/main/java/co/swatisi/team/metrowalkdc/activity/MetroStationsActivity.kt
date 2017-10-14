package co.swatisi.team.metrowalkdc.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.View
import co.swatisi.team.metrowalkdc.utility.FetchMetroStationsManager
import co.swatisi.team.metrowalkdc.adapter.MetroStationsAdapter
import co.swatisi.team.metrowalkdc.R
import kotlinx.android.synthetic.main.activity_metro_stations.*
import android.app.SearchManager
import android.content.Context
import android.widget.SearchView
import co.swatisi.team.metrowalkdc.model.Station
import co.swatisi.team.metrowalkdc.utility.Constants
import org.jetbrains.anko.activityUiThread
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

class MetroStationsActivity : AppCompatActivity() {

    private var recyclerViewList: List<Station> = listOf()

    private lateinit var menu: Menu
    private lateinit var fetchMetroStationsManager: FetchMetroStationsManager
    private lateinit var staggeredLayoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: MetroStationsAdapter

    private val onItemClickListener = object : MetroStationsAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int, filteredList: List<Station>) {
            val intent = Intent(this@MetroStationsActivity, LandmarksActivity::class.java)
            intent.putExtra(getString(R.string.landmarks_intent_extra_lat_name), filteredList[position].lat)
            intent.putExtra(getString(R.string.landmarks_intent_extra_lon_name), filteredList[position].lon)
            // Start the LandmarksActivity with the coordinates of the selected station
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metro_stations)

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of list and populate the view
            recyclerViewList = savedInstanceState.getParcelableArrayList(Constants.LIST_KEY)
            populateRecyclerView(recyclerViewList)
            // Hide the ProgressBar
            stations_progress_bar.visibility = View.GONE
        } else {
            getStationsAndShow()
        }
    }

    private fun getStationsAndShow() {
        fetchMetroStationsManager = FetchMetroStationsManager(this@MetroStationsActivity)

        doAsync {
            recyclerViewList = fetchMetroStationsManager.fetchStations()
            activityUiThread {
                if (recyclerViewList.isNotEmpty()) {
                    populateRecyclerView(recyclerViewList)
                    // Hide the ProgressBar
                    stations_progress_bar.visibility = View.GONE
                } else {
                    toast(getString(R.string.metro_stations_list_empty_error))
                    finish()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelableArrayList(Constants.LIST_KEY, ArrayList<Station>(recyclerViewList))
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present
        menuInflater.inflate(R.menu.search_menu, menu)
        this.menu = menu

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(q: String): Boolean {
                adapter.filter.filter(q)
                return true
            }

            override fun onQueryTextSubmit(q: String): Boolean {
                return false
            }
        })
        return true
    }

    private fun populateRecyclerView(list: List<Station>) {
        staggeredLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        stationsList.layoutManager = staggeredLayoutManager

        adapter = MetroStationsAdapter(list)
        stationsList.adapter = adapter
        adapter.setOnItemClickListener(onItemClickListener)
    }
}

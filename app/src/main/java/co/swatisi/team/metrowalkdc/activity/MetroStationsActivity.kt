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
import co.swatisi.team.metrowalkdc.model.StationData
import kotlinx.android.synthetic.main.activity_metro_stations.*
import android.app.SearchManager
import android.content.Context
import android.util.Log
import android.widget.SearchView
import co.swatisi.team.metrowalkdc.model.Station

class MetroStationsActivity : AppCompatActivity() {
    private val tag = "MetroStationsActivity"
    private lateinit var menu: Menu

    private lateinit var staggeredLayoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: MetroStationsAdapter

    private val onItemClickListener = object : MetroStationsAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int, filteredList: ArrayList<Station>) {
            val intent = Intent(this@MetroStationsActivity, LandmarksActivity::class.java)
            intent.putExtra("latitude", filteredList[position].lat)
            intent.putExtra("longitude", filteredList[position].lon)
            // Start the LandmarksActivity with the coordinates of the selected station
            Log.d(tag, "Clicked on ${filteredList[position].name}")
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metro_stations)

        // Check if we have the metro station data
        if(StationData.stationList().isEmpty()) {
            FetchMetroStationsManager.getStationList(this)
        }

        staggeredLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        stationsList.layoutManager = staggeredLayoutManager

        adapter = MetroStationsAdapter()
        stationsList.adapter = adapter
        adapter.setOnItemClickListener(onItemClickListener)
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
}

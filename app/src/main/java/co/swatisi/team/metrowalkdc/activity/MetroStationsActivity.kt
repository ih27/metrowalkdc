package co.swatisi.team.metrowalkdc.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import co.swatisi.team.metrowalkdc.utility.FetchMetroStationsManager
import co.swatisi.team.metrowalkdc.adapter.MetroStationsAdapter
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.StationData
import kotlinx.android.synthetic.main.activity_metro_stations.*

class MetroStationsActivity : AppCompatActivity() {

    private lateinit var staggeredLayoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: MetroStationsAdapter

    private val onItemClickListener = object : MetroStationsAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            val intent = Intent(this@MetroStationsActivity, LandmarksActivity::class.java)
            intent.putExtra("latitude", StationData.stationList()[position].lat)
            intent.putExtra("longitude", StationData.stationList()[position].lon)
            // Start the LandmarksActivity with the coordinates of the selected station
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
}

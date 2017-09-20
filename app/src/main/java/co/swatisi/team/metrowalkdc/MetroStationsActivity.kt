package co.swatisi.team.metrowalkdc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_metro_stations.*

class MetroStationsActivity : AppCompatActivity() {

    lateinit private var staggeredLayoutManager: StaggeredGridLayoutManager
    lateinit private var adapter: MetroStationsAdapter

    private val onItemClickListener = object : MetroStationsAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metro_stations)

        staggeredLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        stationsList.layoutManager = staggeredLayoutManager

        adapter = MetroStationsAdapter()
        stationsList.adapter = adapter
        adapter.setOnItemClickListener(onItemClickListener)
    }
}

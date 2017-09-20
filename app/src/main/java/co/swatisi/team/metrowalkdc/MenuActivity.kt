package co.swatisi.team.metrowalkdc

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_metro_stations.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Get the metro stations
        FetchMetroStationsAsyncTask.getStationList(this)

        stations_button.setOnClickListener {
            val intent = Intent(this, MetroStationsActivity::class.java)
            startActivity(intent)
        }

  }
}

package co.swatisi.team.metrowalkdc.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import co.swatisi.team.metrowalkdc.utility.FetchMetroStationsManager
import co.swatisi.team.metrowalkdc.R
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Get the metro stations
        FetchMetroStationsManager.getStationList(this)

        // Set the OnClickListeners for each button
        stations_button.setOnClickListener {
            val intent = Intent(this, MetroStationsActivity::class.java)
            startActivity(intent)
        }

        closest_station_button.setOnClickListener {
            val intent = Intent(this, LandmarksActivity::class.java)
            startActivity(intent)
        }

        favorite_landmarks_button.setOnClickListener {
            val intent = Intent(this, LandmarksActivity::class.java)
            intent.putExtra("favorites", true)
            startActivity(intent)
        }
    }
}

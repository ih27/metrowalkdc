package co.swatisi.team.metrowalkdc.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import co.swatisi.team.metrowalkdc.R
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Set the title of the activity
        title = getString(R.string.menu_activity_label)

        // Set the listeners for each button clicks
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
            intent.putExtra(getString(R.string.favorites_intent_extra_name), true)
            startActivity(intent)
        }
    }
}

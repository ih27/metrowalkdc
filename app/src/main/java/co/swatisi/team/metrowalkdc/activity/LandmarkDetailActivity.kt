package co.swatisi.team.metrowalkdc.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.Landmark
import org.jetbrains.anko.toast

class LandmarkDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmark_detail)

        // Obtain landmark from intent
        val landmark = intent.getParcelableExtra<Landmark>("landmark")
        toast(landmark.name)
    }
}

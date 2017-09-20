package co.swatisi.team.metrowalkdc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val testJsonObject = FetchMetroStationsAsyncTask.getStationList(this)

        testJsonObject?.let {
            test_json_text.text = testJsonObject.toString()
        }
  }
}

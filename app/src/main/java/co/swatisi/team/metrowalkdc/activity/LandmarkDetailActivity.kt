package co.swatisi.team.metrowalkdc.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.Landmark
import co.swatisi.team.metrowalkdc.utility.PersistenceManager
import kotlinx.android.synthetic.main.activity_landmark_detail.*
import android.content.Intent
import android.text.Html
import android.text.method.LinkMovementMethod
import co.swatisi.team.metrowalkdc.utility.Constants


class LandmarkDetailActivity : AppCompatActivity() {
    private val tag = "LandmarkDetailActivity"
    private lateinit var menu: Menu
    private var isFavorite = false
    private lateinit var landmark: Landmark
    private lateinit var persistenceManager: PersistenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmark_detail)

        // Obtain landmark from intent
        landmark = intent.getParcelableExtra<Landmark>("landmark")

        // Set the values
        setLayoutValues()

        // Get the persistence manager for favorites functionality
        persistenceManager = PersistenceManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present
        menuInflater.inflate(R.menu.landmark_detail_menu, menu)
        this.menu = menu

        // If the landmark is saved to the favorites, change the icon
        if(persistenceManager.isFavorite(landmark))
            favoriteButtonOn()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorite_menu_item -> {
                // Favorite button clicked
                toggle()
                true
            }
            R.id.share_menu_item -> {
                // Share button clicked
                chooseSharingApp()
                true
            }
            else ->
                // The action is not recognized
                super.onOptionsItemSelected(item)
        }
    }

    private fun setLayoutValues() {
        landmark_detail_name.text = landmark.name
        landmark_detail_rating.rating = landmark.rating.toFloat()
        landmark_detail_review.text = String.format(getString(R.string.landmark_detail_review,
                landmark.reviewCount))
        landmark_detail_website.text = Html.fromHtml(String.format(getString(R.string.landmark_detail_website,
                Constants.LANDMARK_URLBASE + landmark.id)))
        landmark_detail_website.movementMethod = LinkMovementMethod.getInstance()
        landmark_detail_address.text = String.format(getString(R.string.landmark_detail_address,
                landmark.displayAddress))
    }

    private fun chooseSharingApp() {
        // Share button implicit intent
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = getString(R.string.share_mime_type)
        shareIntent.putExtra(Intent.EXTRA_TEXT, landmark.textToShare())
        startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.share_intent_title)))
    }

    private fun toggle() {
        if (isFavorite) {
            removeFavorite(landmark)
            favoriteButtonOff()
        } else {
            addFavorite(landmark)
            favoriteButtonOn()
        }
    }

    private fun removeFavorite(landmark: Landmark) {
        persistenceManager.deleteLandmark(landmark)
    }

    private fun addFavorite(landmark: Landmark) {
        persistenceManager.saveLandmark(landmark)
    }

    private fun favoriteButtonOn() {
        val item = menu.findItem(R.id.favorite_menu_item)
        item.setIcon(R.drawable.ic_favorite_on)
        isFavorite = true
    }

    private fun favoriteButtonOff() {
        val item = menu.findItem(R.id.favorite_menu_item)
        item.setIcon(R.drawable.ic_favorite_off)
        isFavorite = false
    }
}

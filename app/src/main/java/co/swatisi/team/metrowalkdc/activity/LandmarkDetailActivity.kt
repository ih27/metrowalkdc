package co.swatisi.team.metrowalkdc.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.support.v7.widget.ShareActionProvider
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.Landmark
import co.swatisi.team.metrowalkdc.utility.PersistenceManager
import kotlinx.android.synthetic.main.activity_landmark_detail.*
import android.content.Intent
import android.support.v4.view.MenuItemCompat


class LandmarkDetailActivity : AppCompatActivity() {
    private val tag = "LandmarkDetailActivity"
    private lateinit var menu: Menu
    private var isFavorite = false
    private lateinit var landmark: Landmark
    private lateinit var persistenceManager: PersistenceManager
    private var shareActionProvider: ShareActionProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmark_detail)

        // Obtain landmark from intent
        landmark = intent.getParcelableExtra<Landmark>("landmark")

        // Set the values
        landmark_detail_name.text = landmark.name
        landmark_detail_rating.rating = landmark.rating.toFloat()
        landmark_detail_review.text = String.format(getString(R.string.landmark_detail_review,
                landmark.reviewCount))

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

        // Locate MenuItem with ShareActionProvider
        val shareButton = menu.findItem(R.id.share_menu_item)
        // Fetch and store ShareActionProvider
        shareActionProvider = MenuItemCompat.getActionProvider(shareButton) as ShareActionProvider
        setShareIntent()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorite_menu_item -> {
                // Favorite button clicked
                toggle()
                true
            }
            else ->
                // The action is not recognized
                super.onOptionsItemSelected(item)
        }
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

    // Call to set the share intent
    private fun setShareIntent() {
        // Share button implicit intent
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = getString(R.string.share_mime_type)
        shareIntent.putExtra(Intent.EXTRA_TEXT, landmark.textToShare())
        shareActionProvider?.setShareIntent(shareIntent)
    }
}

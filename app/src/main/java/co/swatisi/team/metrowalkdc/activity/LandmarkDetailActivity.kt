package co.swatisi.team.metrowalkdc.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.Landmark
import co.swatisi.team.metrowalkdc.utility.PersistenceManager
import co.swatisi.team.metrowalkdc.utility.FetchLandmarksManager
import kotlinx.android.synthetic.main.activity_landmark_detail.*
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import co.swatisi.team.metrowalkdc.utility.Constants
import com.squareup.picasso.Picasso
import org.jetbrains.anko.toast

class LandmarkDetailActivity : AppCompatActivity() {
    private var isFavorite = false

    private lateinit var menu: Menu
    private lateinit var landmark: Landmark
    private lateinit var persistenceManager: PersistenceManager
    private lateinit var fetchLandmarksManager: FetchLandmarksManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmark_detail)

        // Obtain landmark from intent
        landmark = intent.getParcelableExtra(getString(R.string.landmark_detail_intent_extra_name))

        // Get the required managers
        persistenceManager = PersistenceManager(this)
        fetchLandmarksManager = FetchLandmarksManager(this)

        // Set the layout
        setLayoutValues()
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

    // Set the layout elements
    private fun setLayoutValues() {
        // Load only if the image URL exists, otherwise placeholder is kept
        if (landmark.imageURL.isNotEmpty()) {
            Picasso.with(this).load(landmark.imageURL).into(landmark_detail_image)
        }
        landmark_detail_image.contentDescription = landmark.id
        landmark_detail_name.text = landmark.name
        landmark_detail_rating.rating = landmark.rating.toFloat()
        landmark_detail_review.text = String.format(getString(R.string.landmark_detail_review,
                landmark.reviewCount))

        // Display the phone number if the landmark has it
        if (landmark.displayPhone.isEmpty()) {
            landmark_detail_phone.visibility = View.GONE
        } else {
            landmark_detail_phone.text = String.format(getString(R.string.landmark_detail_phone, landmark.displayPhone))
        }
        // Clickable URL with a simple text "Website"
        landmark_detail_website.text = Html.fromHtml(String.format(getString(R.string.landmark_detail_website,
                Constants.LANDMARK_URLBASE + landmark.id)))
        landmark_detail_website.movementMethod = LinkMovementMethod.getInstance()
        landmark_detail_address.text = String.format(getString(R.string.landmark_detail_address,
                landmark.displayAddress))
    }

    // Present the user a sharing app chooser
    private fun chooseSharingApp() {
        // Share button implicit intent
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = getString(R.string.share_mime_type)
        shareIntent.putExtra(Intent.EXTRA_TEXT, landmark.textToShare())
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_intent_title)))
    }

    // General toggle switch for favorite button
    private fun toggle() {
        if (isFavorite) {
            removeFavorite(landmark)
            favoriteButtonOff()
        } else {
            addFavorite(landmark)
            favoriteButtonOn()
        }
    }

    // Remove the landmark from favorites
    private fun removeFavorite(landmark: Landmark) {
        persistenceManager.deleteLandmark(landmark)
    }

    // Add the landmark as a favorite
    private fun addFavorite(landmark: Landmark) {
        persistenceManager.saveLandmark(landmark)
    }

    // Set the favorite button and flag to on
    private fun favoriteButtonOn() {
        val item = menu.findItem(R.id.favorite_menu_item)
        item.setIcon(R.drawable.ic_favorite_on)
        isFavorite = true
    }

    // Set the favorite button and flag to off
    private fun favoriteButtonOff() {
        val item = menu.findItem(R.id.favorite_menu_item)
        item.setIcon(R.drawable.ic_favorite_off)
        isFavorite = false
    }

    // Start Google Maps walking mode navigation when clicked on the icon
    fun startDirections(view: View) {
        val gmmIntentUri = Uri.parse("google.navigation:q=${landmark.lat},${landmark.lon}&mode=w")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.`package` = "com.google.android.apps.maps"
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            toast(getString(R.string.directions_error))
        }
    }
}

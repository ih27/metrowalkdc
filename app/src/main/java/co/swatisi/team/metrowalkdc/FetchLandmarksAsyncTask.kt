package co.swatisi.team.metrowalkdc

import android.content.Context
import android.location.Location
import android.provider.MediaStore.Video.VideoColumns.CATEGORY
import android.util.Log
import android.widget.Toast
import co.swatisi.team.metrowalkdc.model.StationData
import com.koushikdutta.ion.Ion

/**
 * Created by ihasanov on 9/19/17.
 */

object FetchLandmarksAsyncTask {

    private const val YELP_API = "https://api.yelp.com/v3/businesses/search"
    private const val YELP_API_TOKEN = "Bearer Z4XQpK2rOdiyuruBtz6BAr9hDit9NmkEiav-EcEDr_" +
            "UkfrARs7nS900FcZ8nsG0URpIAlanZi7DaH7SoMxS7mgMD9ArpW5CzQp3NkiUp4YuHqQnRSdvmkqP9W2bIWXYx"
    private const val TAG = "FetchLandmarks"

    // Yelp Search API query parameters
    private const val RADIUS = "1600"
    private const val CATEGORY = "landmarks"
    private const val SORT_BY = "distance"


    fun getLandmarksList(context: Context, location: Location?) = try {
        Log.d(TAG, "Before API call: ${location.toString()}")
        val jsonLandmarks = Ion.with(context).load(YELP_API)
                .addHeader("Authorization", YELP_API_TOKEN)
                .addQuery("latitude", location?.latitude.toString())
                .addQuery("longitude", location?.longitude.toString())
                .addQuery("radius", RADIUS)
                .addQuery("categories", CATEGORY)
                .addQuery("sort_by", SORT_BY)
                .asJsonObject().get()
        jsonLandmarks?.let {
            Toast.makeText(context, jsonLandmarks.asString, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Landmarks List Received.")
        }
    } catch (e: Exception) {
        Log.e(TAG, e.message)
    }
}
package co.swatisi.team.metrowalkdc

import android.content.Context
import android.location.Location
import android.util.Log
import co.swatisi.team.metrowalkdc.model.StationData
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion

class FetchLandmarksManager(val context: Context, val location: Location?) {

    private val YELP_API = "https://api.yelp.com/v3/businesses/search"
    private val YELP_API_TOKEN = "Bearer Z4XQpK2rOdiyuruBtz6BAr9hDit9NmkEiav-EcEDr_" +
            "UkfrARs7nS900FcZ8nsG0URpIAlanZi7DaH7SoMxS7mgMD9ArpW5CzQp3NkiUp4YuHqQnRSdvmkqP9W2bIWXYx"
    private val TAG = "FetchLandmarks"

    // Yelp Search API query parameters
    private val RADIUS = "1600"
    private val CATEGORY = "landmarks"
    private val SORT_BY = "distance"

    fun getLandmarksList(): JsonObject? {
        var jsonLandmarks = JsonObject()
        try {
            jsonLandmarks = Ion.with(context).load(YELP_API)
                    .addHeader("Authorization", YELP_API_TOKEN)
                    .addQuery("latitude", location?.latitude.toString())
                    .addQuery("longitude", location?.longitude.toString())
                    .addQuery("radius", RADIUS)
                    .addQuery("categories", CATEGORY)
                    .addQuery("sort_by", SORT_BY)
                    .asJsonObject().get()
            jsonLandmarks?.let {
                StationData.updateList(jsonLandmarks)
                Log.d(TAG, "Landmarks List Received.")
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
        return jsonLandmarks
    }
}
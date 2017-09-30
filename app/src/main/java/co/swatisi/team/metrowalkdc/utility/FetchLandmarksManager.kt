package co.swatisi.team.metrowalkdc.utility

import android.content.Context
import android.util.Log
import co.swatisi.team.metrowalkdc.model.LandmarkData
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion

class FetchLandmarksManager(val context: Context, private val lat: Double, private val lon: Double) {

    private val tag = "FetchLandmarks"

    fun getLandmarksList(): JsonObject? {
        var jsonLandmarks = JsonObject()
        try {
            jsonLandmarks = Ion.with(context).load(Constants.YELP_API)
                    .addHeader("Authorization", Constants.YELP_API_TOKEN)
                    .addQuery("latitude", lat.toString())
                    .addQuery("longitude", lon.toString())
                    .addQuery("radius", Constants.RADIUS)
                    .addQuery("categories", Constants.CATEGORY)
                    .addQuery("limit", Constants.LIMIT)
                    .addQuery("sort_by", Constants.SORT_BY)
                    .asJsonObject().get()
            jsonLandmarks?.let {
                LandmarkData.updateList(jsonLandmarks)
                Log.d(tag, "Landmarks List Received.")
            }
        } catch (e: Exception) {
            Log.e(tag, e.message)
        }
        return jsonLandmarks
    }
}
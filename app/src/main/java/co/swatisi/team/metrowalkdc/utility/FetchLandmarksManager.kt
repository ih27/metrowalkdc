package co.swatisi.team.metrowalkdc.utility

import android.content.Context
import android.net.Uri
import android.util.Log
import co.swatisi.team.metrowalkdc.model.Landmark
import co.swatisi.team.metrowalkdc.model.LandmarkData
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion

class FetchLandmarksManager(val context: Context, private val lat: Double = 0.0, private val lon: Double = 0.0) {

    private val tag = "FetchLandmarks"

    fun getLandmarksList(): JsonObject? {
        var jsonLandmarks = JsonObject()
        try {
            jsonLandmarks = Ion.with(context).load(Constants.YELP_SEARCH_API)
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

    fun isOpenNow(id: String): Boolean {
        var isOpen = false
        try {
            val jsonLandmark = Ion.with(context).load(String.format(Constants.YELP_LANDMARK_API,id))
                    .addHeader("Authorization", Constants.YELP_API_TOKEN)
                    .asJsonObject().get()
            jsonLandmark?.let {
                val hours = jsonLandmark.getAsJsonArray("hours")
                hours?.let {
                    isOpen = hours[0].asJsonObject.get("is_open_now").asBoolean
                    Log.d(tag, "$id : $isOpen")
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "$id : ${e.message}")
        }
        return isOpen
    }
}
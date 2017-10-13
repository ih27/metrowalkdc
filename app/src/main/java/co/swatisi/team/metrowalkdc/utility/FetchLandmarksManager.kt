package co.swatisi.team.metrowalkdc.utility

import android.content.Context
import android.util.Log
import co.swatisi.team.metrowalkdc.model.Landmark
import co.swatisi.team.metrowalkdc.model.LandmarkData
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion

class FetchLandmarksManager(val context: Context, private val lat: Double = 0.0, private val lon: Double = 0.0) {
    private val tag = "FetchLandmarks"

    // Return the list of landmarks or empty list
    fun fetchLandmarks(): List<Landmark> {
        var list = mutableListOf<Landmark>()
        val landmarks = getLandmarksList()

        // Landmarks JSON object empty check
        if (landmarks.entrySet().size != 0) {
            // Success fetching
            if (landmarks.get("total")?.asInt != 0) {
                // One or more landmarks returned
                // Check if we have the landmark data
                if (LandmarkData.landmarkList().isEmpty()) {
                    LandmarkData.updateList(landmarks)
                }
                // Assign the list
                list = LandmarkData.landmarkList()
            }
            // Zero landmark within the radius
        }
        // Failure fetching, so the list is still empty
        return list
    }

    // Get the JSON landmarks list and parse it to the LandmarkData model
    private fun getLandmarksList(): JsonObject {
        // Get Yelp Auth Token
        val yelpAuthToken = YelpAuthManager.getToken(context)

        var jsonLandmarks = JsonObject()
        try {
            jsonLandmarks = Ion.with(context).load(Constants.YELP_SEARCH_API).setTimeout(15 * 1000) // 15 secs
                    .addHeader("Authorization", yelpAuthToken)
                    .addQuery("latitude", lat.toString())
                    .addQuery("longitude", lon.toString())
                    .addQuery("radius", Constants.RADIUS)
                    .addQuery("categories", Constants.CATEGORY)
                    .addQuery("limit", Constants.LIMIT)
                    .addQuery("sort_by", Constants.SORT_BY)
                    .asJsonObject().get()
            jsonLandmarks?.let {
                LandmarkData.updateList(jsonLandmarks)
            }
        } catch (e: Exception) {
            Log.e(tag, e.message)
        }
        return jsonLandmarks
    }
}
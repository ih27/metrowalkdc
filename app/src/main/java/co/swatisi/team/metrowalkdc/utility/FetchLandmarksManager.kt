package co.swatisi.team.metrowalkdc.utility

import android.content.Context
import android.util.Log
import co.swatisi.team.metrowalkdc.model.Landmark
import co.swatisi.team.metrowalkdc.model.LandmarkData
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion

class FetchLandmarksManager(val context: Context, private val lat: Double = 0.0, private val lon: Double = 0.0) {
    private val tag = "FetchLandmarks"

    fun fetchLandmarks(): List<Landmark> {
        var list = mutableListOf<Landmark>()
        val landmarks = getLandmarksList()
        Log.d(tag, "Landmarks: $landmarks")

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

    private fun getLandmarksList(): JsonObject {
        // Get Yelp Auth Token
        val yelpAuthToken = YelpAuthManager.getToken(context)

        var jsonLandmarks = JsonObject()
        try {
            jsonLandmarks = Ion.with(context).load(Constants.YELP_SEARCH_API)
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
                Log.d(tag, "Landmarks List Received.")
            }
        } catch (e: Exception) {
            Log.e(tag, e.message)
        }
        return jsonLandmarks
    }

    fun isOpenNow(id: String): Boolean {
        // Get Yelp Auth Token
        val yelpAuthToken = YelpAuthManager.getToken(context)

        var isOpen = false
        try {
            val jsonLandmark = Ion.with(context).load(String.format(Constants.YELP_LANDMARK_API,id))
                    .addHeader("Authorization", yelpAuthToken)
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
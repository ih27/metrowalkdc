package co.swatisi.team.metrowalkdc.model

import android.net.Uri
import android.provider.SyncStateContract
import co.swatisi.team.metrowalkdc.utility.Constants
import com.google.gson.JsonObject
import java.util.ArrayList

object LandmarkData {
    // ArrayList variable to hold landmarks data
    private var landmarkList: ArrayList<Landmark> = arrayListOf()

    // Get the landmarks list
    fun landmarkList(): ArrayList<Landmark> {
        return landmarkList
    }

    // Given the JSON object, update the landmarks list
    fun updateList(jsonObject: JsonObject) {
        landmarkList = arrayListOf()
        val jsonArray = jsonObject.getAsJsonArray("businesses")
        for (i in 0 until jsonArray.size()) {
            val name = jsonArray.get(i).asJsonObject.get("name").asString
            val imageUrl = Uri.parse(jsonArray.get(i).asJsonObject.get("image_url").asString)
            val address = jsonArray.get(i).asJsonObject.get("location").asJsonObject
                    .getAsJsonArray("display_address").get(0).asString
            val coordinates = jsonArray.get(i).asJsonObject.get("coordinates")
            val lat = coordinates.asJsonObject.get("latitude").asDouble
            val lon = coordinates.asJsonObject.get("longitude").asDouble
            val distanceMeters = jsonArray.get(i).asJsonObject.get("distance").asDouble
            val distance = Math.round(distanceMeters * Constants.MILES_CONVERSION * 100) / 100.toDouble()
            val rating = jsonArray.get(i).asJsonObject.get("rating").asInt
            val landmark = Landmark(name, imageUrl, address, lat, lon, distance, rating)
            landmarkList.add(landmark)
        }
    }

    // Given the coordinates, get the landmark
    fun getLandmark(lat: Double, lon: Double): Landmark? {
        return landmarkList.find {
            it.lat == lat && it.lon == lon
        }
    }
}
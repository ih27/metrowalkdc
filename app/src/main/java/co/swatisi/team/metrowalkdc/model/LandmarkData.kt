package co.swatisi.team.metrowalkdc.model

import co.swatisi.team.metrowalkdc.utility.Constants
import com.google.gson.JsonObject
import java.util.ArrayList

object LandmarkData {
    // List variable to hold landmarks data
    private var landmarkList: MutableList<Landmark> = mutableListOf()

    // Get the landmarks list
    fun landmarkList(): MutableList<Landmark> {
        return landmarkList
    }

    // Given the JSON object, update the landmarks list
    fun updateList(jsonObject: JsonObject) {
        landmarkList = mutableListOf()
        val jsonArray = jsonObject.getAsJsonArray("businesses")
        for (i in 0 until jsonArray.size()) {
            val id = jsonArray.get(i).asJsonObject.get("id").asString
            val name = jsonArray.get(i).asJsonObject.get("name").asString
            val imageUrl = jsonArray.get(i).asJsonObject.get("image_url").asString
            val address = jsonArray.get(i).asJsonObject.get("location").asJsonObject
                    .getAsJsonArray("display_address").get(0).asString
            val phone = jsonArray.get(i).asJsonObject.get("display_phone").asString
            val coordinates = jsonArray.get(i).asJsonObject.get("coordinates")
            val lat = coordinates.asJsonObject.get("latitude").asDouble
            val lon = coordinates.asJsonObject.get("longitude").asDouble
            val distanceMeters = jsonArray.get(i).asJsonObject.get("distance").asDouble
            val distance = Math.round(distanceMeters * Constants.MILES_CONVERSION * 100) / 100.toDouble()
            val rating = jsonArray.get(i).asJsonObject.get("rating").asDouble
            val reviewCount = jsonArray.get(i).asJsonObject.get("review_count").asInt
            val landmark = Landmark(id, name, imageUrl, address, phone, lat, lon, rating, reviewCount, distance)
            landmarkList.add(landmark)
        }
    }

    // Given the JSON object, return a landmark
    fun getLandmark(jsonObject: JsonObject): Landmark {
        val id = jsonObject.get("id").asString
        val name = jsonObject.get("name").asString
        val imageUrl = jsonObject.get("image_url").asString
        val address = jsonObject.get("location").asJsonObject
                .getAsJsonArray("display_address").get(0).asString
        val phone = jsonObject.get("display_phone").asString
        val coordinates = jsonObject.get("coordinates")
        val lat = coordinates.asJsonObject.get("latitude").asDouble
        val lon = coordinates.asJsonObject.get("longitude").asDouble
        val rating = jsonObject.get("rating").asDouble
        val reviewCount = jsonObject.get("review_count").asInt
        return Landmark(id, name, imageUrl, address, phone, lat, lon, rating, reviewCount)
    }

    // Given the coordinates, get the landmark
    fun getLandmark(lat: Double, lon: Double): Landmark? {
        return landmarkList.find {
            it.lat == lat && it.lon == lon
        }
    }
}
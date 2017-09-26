package co.swatisi.team.metrowalkdc.model

import com.google.gson.JsonObject
import java.net.URL
import java.util.ArrayList

object LandmarkData {

    private const val TAG = "LandmarkData"
    private var landmarkList = arrayListOf<Landmark>()

    fun landmarkList(): ArrayList<Landmark> {
        return landmarkList
    }

    fun updateList(jsonObject: JsonObject) {
        val jsonArray = jsonObject.getAsJsonArray("businesses")
        for (i in 0 until jsonArray.size()) {
            val name = jsonArray.get(i).asJsonObject.get("name").asString
            val imageUrl = URL(jsonArray.get(i).asJsonObject.get("image_url").asString)
            val address = jsonArray.get(i).asJsonObject.get("location").asJsonObject
                    .getAsJsonArray("display_address").get(0).asString
            val coordinates = jsonArray.get(i).asJsonObject.get("coordinates")
            val lat = coordinates.asJsonObject.get("latitude").asFloat
            val lon = coordinates.asJsonObject.get("longitude").asFloat
            val distance = jsonArray.get(i).asJsonObject.get("distance").asFloat
            val landmark = Landmark(name, imageUrl, address, lat, lon, distance)
            landmarkList.add(landmark)
        }
    }
}
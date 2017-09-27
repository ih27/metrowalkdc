package co.swatisi.team.metrowalkdc.model

import com.google.gson.JsonObject
import java.util.ArrayList

object StationData {

    private var stationList = arrayListOf<Station>()

    fun stationList(): ArrayList<Station> {
        return stationList
    }

    fun updateList(jsonObject: JsonObject) {
        val jsonArray = jsonObject.getAsJsonArray("Stations")
        for (i in 0 until jsonArray.size()) {
            val name = jsonArray.get(i).asJsonObject.get("Name").asString
            val lat = jsonArray.get(i).asJsonObject.get("Lat").asFloat
            val lon = jsonArray.get(i).asJsonObject.get("Lon").asFloat
            val lineCode = jsonArray.get(i).asJsonObject.get("LineCode1").asString
            val station = Station(name, lat, lon, lineCode)
            stationList.add(station)
        }
    }
}
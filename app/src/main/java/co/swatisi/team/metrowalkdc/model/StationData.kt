package co.swatisi.team.metrowalkdc.model

import com.google.gson.JsonObject
import java.util.ArrayList

object StationData {
    // ArrayList variable to hold metro stations data
    private var stationList = arrayListOf<Station>()

    // Get the metro stations list
    fun stationList(): ArrayList<Station> {
        return stationList
    }

    // Given the JSON object, update the metro stations list
    fun updateList(jsonObject: JsonObject) {
        stationList = arrayListOf()
        val jsonArray = jsonObject.getAsJsonArray("Stations")
        for (i in 0 until jsonArray.size()) {
            val name = jsonArray.get(i).asJsonObject.get("Name").asString
            val lat = jsonArray.get(i).asJsonObject.get("Lat").asDouble
            val lon = jsonArray.get(i).asJsonObject.get("Lon").asDouble
            val lineCode = jsonArray.get(i).asJsonObject.get("LineCode1").asString
            val station = Station(name, lat, lon, lineCode)
            stationList.add(station)
        }
    }

    // Given the coordinates, get the station
    fun getStation(lat: Double, lon: Double): Station? {
        return stationList.find {
            it.lat == lat && it.lon == lon
        }
    }
}
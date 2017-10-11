package co.swatisi.team.metrowalkdc.model

import com.google.gson.JsonObject

object StationData {
    // List variable to hold metro stations data
    private var stationList = mutableListOf<Station>()

    // Get the metro station list, sorted by name
    fun stationList(): List<Station> {
        return stationList.sortedBy { it }
    }

    // Given the JSON object, update the metro station list
    fun updateList(jsonObject: JsonObject) {
        stationList = mutableListOf()
        val jsonArray = jsonObject.getAsJsonArray("Stations")
        for (i in 0 until jsonArray.size()) {
            val name = jsonArray.get(i).asJsonObject.get("Name").asString
            val lat = jsonArray.get(i).asJsonObject.get("Lat").asDouble
            val lon = jsonArray.get(i).asJsonObject.get("Lon").asDouble
            val lineCode1 = jsonArray.get(i).asJsonObject.get("LineCode1").asString
            val lineCode2Json = jsonArray.get(i).asJsonObject.get("LineCode2")
            val lineCode2 = if (!lineCode2Json.isJsonNull) lineCode2Json.asString else ""
            val lineCode3Json = jsonArray.get(i).asJsonObject.get("LineCode3")
            val lineCode3 = if (!lineCode3Json.isJsonNull) lineCode3Json.asString else ""
            val station = Station(name, lat, lon, lineCode1, lineCode2, lineCode3)
            stationList.add(station)
            // Due to the way WMATA API has assigned line codes, we get duplicate records
        }
    }
}
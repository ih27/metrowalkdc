package co.swatisi.team.metrowalkdc.utility

import android.content.Context
import android.util.Log
import co.swatisi.team.metrowalkdc.model.Station
import co.swatisi.team.metrowalkdc.model.StationData
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion

class FetchMetroStationsManager(val context: Context) {

    private val tag = "FetchMetroStations"

    fun fetchStations(): List<Station> {
        var list = listOf<Station>()
        val stations = getStationList(context)

        // Stations JSON object empty check
        if (stations.entrySet().size != 0) {
            // Success fetching
            // Check if we have the station data
            if (StationData.stationList().isEmpty()) {
                StationData.updateList(stations)
            }
            // Assign the list
            list = StationData.stationList()
        }
        // Failure fetching, so the list is still empty

        return list
    }

    fun getStationList(context: Context): JsonObject {
        var jsonStations = JsonObject()
        try {
            jsonStations = Ion.with(context).load(Constants.METRO_API)
                    .addHeader("api_key", Constants.METRO_API_TOKEN)
                    .asJsonObject().get()
            jsonStations?.let {
                StationData.updateList(jsonStations)
                Log.d(tag, "Station Data Updated.")
            }
        } catch (e: Exception) {
            Log.e(tag, e.message)
        }
        return jsonStations
    }
}


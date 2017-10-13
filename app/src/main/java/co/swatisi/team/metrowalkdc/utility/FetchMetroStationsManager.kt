package co.swatisi.team.metrowalkdc.utility

import android.content.Context
import android.util.Log
import co.swatisi.team.metrowalkdc.model.Station
import co.swatisi.team.metrowalkdc.model.StationData
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion

class FetchMetroStationsManager(val context: Context) {

    private val tag = "FetchMetroStations"

    // Return the list of metro stations or empty list
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

    // Get the JSON metro list and parse it to the StationData model
    private fun getStationList(context: Context): JsonObject {
        var jsonStations = JsonObject()
        try {
            jsonStations = Ion.with(context).load(Constants.METRO_API).setTimeout(5 * 1000) // 5 secs
                    .addHeader("api_key", Constants.METRO_API_TOKEN)
                    .asJsonObject().get()
            jsonStations?.let {
                StationData.updateList(jsonStations)
            }
        } catch (e: Exception) {
            Log.e(tag, e.message)
        }
        return jsonStations
    }
}


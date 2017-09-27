package co.swatisi.team.metrowalkdc.utility

import android.content.Context
import android.util.Log
import co.swatisi.team.metrowalkdc.model.StationData
import com.koushikdutta.ion.Ion

object FetchMetroStationsManager {

    private val tag = "FetchMetroStations"

    fun getStationList(context: Context) = try {
        val jsonStations = Ion.with(context).load(Constants.METRO_API)
                .addHeader("api_key", Constants.METRO_API_TOKEN)
                .asJsonObject().get()
        jsonStations?.let {
            StationData.updateList(jsonStations)
            Log.d(tag, "Station Data Updated.")
        }
    } catch (e: Exception) {
        Log.e(tag, e.message)
    }
}


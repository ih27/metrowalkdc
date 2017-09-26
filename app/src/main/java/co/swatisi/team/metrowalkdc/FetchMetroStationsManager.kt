package co.swatisi.team.metrowalkdc

import android.content.Context
import android.util.Log
import co.swatisi.team.metrowalkdc.model.StationData
import com.koushikdutta.ion.Ion

object FetchMetroStationsManager {

    private const val METRO_API = "https://api.wmata.com/Rail.svc/json/jStations"
    private const val METRO_API_TOKEN = "372279dd0fae484ea9c426f476031703"
    private const val TAG = "FetchMetroStations"

    fun getStationList(context: Context) = try {
        val jsonStations = Ion.with(context).load(METRO_API)
                .addHeader("api_key", METRO_API_TOKEN)
                .asJsonObject().get()
        jsonStations?.let {
            StationData.updateList(jsonStations)
            Log.d(TAG, "Station Data Updated.")
        }
    } catch (e: Exception) {
        Log.e(TAG, e.message)
    }
}


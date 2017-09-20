package co.swatisi.team.metrowalkdc

import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion

/**
 * Created by ihasanov on 9/19/17.
 */
object FetchMetroStationsAsyncTask {

    private const val METRO_API = "https://api.wmata.com/Rail.svc/json/jStations"
    private const val METRO_API_TOKEN = "372279dd0fae484ea9c426f476031703"
    private const val TAG = "FetchMetroStations"

    fun getStationList(context: Context): JsonObject? = try {
        Ion.with(context).load(METRO_API)
                .addHeader("api_key", METRO_API_TOKEN)
                .asJsonObject().get()
    } catch (e: Exception) {
        Log.e(TAG, e.message)
        null
    }
}


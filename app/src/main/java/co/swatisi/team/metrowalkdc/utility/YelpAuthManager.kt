package co.swatisi.team.metrowalkdc.utility

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion

object YelpAuthManager {
    private val tag = "YelpAuthManager"

    fun getToken(context: Context): String {
        var authToken = ""
        try {
            val result = Ion.with(context).load(Constants.YELP_AUTH_API).setTimeout(10 * 1000) // 10 secs
                    .setBodyParameter("grant_type", Constants.YELP_AUTH_GRANT_TYPE)
                    .setBodyParameter("client_id", Constants.YELP_AUTH_CLIENT_ID)
                    .setBodyParameter("client_secret", Constants.YELP_AUTH_CLIENT_SECRET)
                    .asString().get()
            val resultJson = Gson().fromJson(result, JsonObject::class.java)
            val accessToken = resultJson.get("access_token").asString
            val tokenType = resultJson.get("token_type").asString
            authToken = "$tokenType $accessToken"
        } catch (e: Exception) {
            Log.e(tag, e.message)
        }
        return authToken
    }
}

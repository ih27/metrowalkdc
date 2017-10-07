package co.swatisi.team.metrowalkdc.utility

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import co.swatisi.team.metrowalkdc.model.Landmark
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PersistenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    // Save the landmark
    fun saveLandmark(landmark: Landmark) {
        val favorites = fetchFavorites().toMutableList()
        favorites.add(landmark)
        commitChanges(favorites)
    }

    // Delete the landmark from the shared preferences
    fun deleteLandmark(landmark: Landmark) {
        val favorites = fetchFavorites().toMutableList()
        favorites.remove(landmark)
        commitChanges(favorites)
    }

    // Get the favorites list from the shared preferences
    fun fetchFavorites(): List<Landmark> {
        val favoritesJson = sharedPreferences.getString(Constants.FAVORITES_PREF_KEY, null)
        return if(favoritesJson == null) {
            arrayListOf<Landmark>()
        } else {
            val favoritesType = object : TypeToken<MutableList<Landmark>>() {}.type
            Gson().fromJson(favoritesJson, favoritesType)
        }
    }

    // Is the landmark favorite?
    fun isFavorite(landmark: Landmark): Boolean {
        return fetchFavorites().contains(landmark)
    }

    // Utility function to commit changes to the editor
    private fun commitChanges(list: List<Landmark>) {
        val editor = sharedPreferences.edit()
        editor.putString(Constants.FAVORITES_PREF_KEY, Gson().toJson(list))
        editor.apply()
    }
}
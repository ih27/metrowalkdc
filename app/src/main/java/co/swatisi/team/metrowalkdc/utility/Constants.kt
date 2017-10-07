package co.swatisi.team.metrowalkdc.utility

object Constants {

    // Location permission request code
    const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    // WMATA API related constants
    const val METRO_API = "https://api.wmata.com/Rail.svc/json/jStations"
    const val METRO_API_TOKEN = "372279dd0fae484ea9c426f476031703"

    // YELP Fusion API related constants
    const val YELP_SEARCH_API = "https://api.yelp.com/v3/businesses/search"
    const val YELP_LANDMARK_API = "https://api.yelp.com/v3/businesses/%s"
    const val YELP_API_TOKEN = "Bearer Z4XQpK2rOdiyuruBtz6BAr9hDit9NmkEiav-EcEDr_" +
            "UkfrARs7nS900FcZ8nsG0URpIAlanZi7DaH7SoMxS7mgMD9ArpW5CzQp3NkiUp4YuHqQnRSdvmkqP9W2bIWXYx"
    const val RADIUS = "1600" /* 1 mile = 1600 meters */
    const val CATEGORY = "landmarks"
    const val SORT_BY = "rating"
    const val LIMIT = "10"
    const val MILES_CONVERSION = 0.000625

    val FAVORITES_PREF_KEY = "FAVORITES"


}
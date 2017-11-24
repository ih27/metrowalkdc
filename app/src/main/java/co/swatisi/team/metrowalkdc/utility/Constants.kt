package co.swatisi.team.metrowalkdc.utility

object Constants {

    // Location permission request code
    const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    // WMATA API related constants
    const val METRO_API = "https://api.wmata.com/Rail.svc/json/jStations"
    const val METRO_API_TOKEN = "WRITE-YOUR-OWN-KEY"

    // YELP Fusion API related constants
    const val YELP_AUTH_API = "https://api.yelp.com/oauth2/token"
    const val YELP_AUTH_GRANT_TYPE = "client_credentials"
    const val YELP_AUTH_CLIENT_ID = "WRITE-YOUR-OWN-KEY"
    const val YELP_AUTH_CLIENT_SECRET = "WRITE-YOUR-OWN-KEY"

    const val YELP_SEARCH_API = "https://api.yelp.com/v3/businesses/search"
    const val RADIUS = "1600" /* 1 mile = 1600 meters */
    const val CATEGORY = "landmarks"
    const val SORT_BY = "rating"
    const val LIMIT = "10"
    const val MILES_CONVERSION = 0.000625
    const val LANDMARK_URLBASE = "https://www.yelp.com/biz/"

    // Various key labels used throughout the app
    const val FAVORITES_PREF_KEY = "FAVORITES"
    const val LANDMARKS_LABEL_KEY = "LABEL"
    const val LIST_KEY = "LIST"
    const val FUNCTIONALITY_KEY = "FUNCTIONALITY"
}

# metro-explorer-team-swatisi
MetroWalkDC app
by Ismayil and Swati

# TODO #

* Utilize the App Bar for menu functionality in your LandmarkDetailActivity to provide buttons to favorite and share
* Persist favorited Landmarks using SharedPreferences
* Share functionality should use an Implicit Intent
* Walking directions functionality should use an Explicit Intent
* Save to favorites
* Get walking directions
* Share it with a friend
* Allow the user to view a list of their favorite landmarks
* Never crash or leave user in a bad state! If something goes wrong, present a helpful Dialog to the user
* Support portrait and landscape mode (UI components should not be cut off if user switches to landscape) and prevent this rotation from destroying the activity (recommendation is to provide both a portrait and landscape XML layout for each screen and to utilize onSaveInstanceState)
* Support 2 languages (English and a language of your choice - use http://translate.google.com if you don't know another language). You don't need to dynamically translate JSON WMATA or Yelp data, but you should have translations for your app title, settings screen labels, etc.. For example, you don't need translations for the phrase "Foggy Bottom" that comes from the JSON.
* Don't forget to create a custom app launcher icon and include it at multiple resolutions

# DONE #
Utilize the App Bar for menu functionality in your MetroStationActivity to allow the user to type a few letters to filter the station list

Detect the device's location

Allow the user to choose whether he or she wants to let the app choose the nearest metro station or manually pick a metro station 

Display the top 10 landmarks/historic buildings around the selected metro station

Select a landmark, and allow user to
* View details such as name, image, address, and distance away from metro station
* MenuActivity (3 buttons - 1. Use Closest Station, 2. Select a Station, 3. Favorite Landmarks)
* MetroStationActivity
* LandmarksActivity
* LandmarkDetailActivity

Use the WMATA and Yelp API’s with response type JSON

Retrieve and parse metro data from the WMATA API

Retrieve and parse business data from the Yelp Fusion API

Both WMATA and Yelp data should be downloaded on a background thread using a black box “manager class”

Feel free to use Ion or another networking library to fetch your data, but it should be inside your AsyncTask (see our Trivia app)
the user should see an indeterminate ProgressDialog while they are waiting. The result should be array of model objects (e.g. Station objects or Landmark objects), it should not return a JSON object

Use a third party library to download image icons

The metro stations and landmarks should be displayed using RecyclerView

Location should be obtained by using Android’s fused location provider. LocationManager’s onLocationChanged callback method - do not rely solely on getLastKnownLocation
* request location updates, timeout after 10 seconds and fall back on last known location, if it exists…..otherwise fail
* the user should see an indeterminate ProgressDialog while they are waiting for the location
* Your location code should be in it’s own class (e.g. LocationDetector.kt) - this class will be a “blackbox” to the activity, and will define an interface to allow it to “callback” the location to the activity

Handle location runtime permissions properly

Utilize “black box” classes for encapsulating functinoality - (e.g. LocationDetector.kt, PersistanceManager.kt, etc..)

No hard-coded Strings! Use strings.xml

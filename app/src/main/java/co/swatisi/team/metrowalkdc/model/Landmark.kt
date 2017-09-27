package co.swatisi.team.metrowalkdc.model

import android.net.Uri

data class Landmark(val name: String, val imageURL: Uri, val displayAddress: String,
                    val lat: Float, val lon: Float, val distance: Float)
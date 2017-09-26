package co.swatisi.team.metrowalkdc.model

import java.net.URL

data class Landmark(val name: String, val imageURL: URL, val displayAddress: String,
                    val lat: Float, val lon: Float, val distance: Float)
package co.swatisi.team.metrowalkdc.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Landmark(val id: String, val name: String, val imageURL: String, val displayAddress: String,
                    val lat: Double, val lon: Double, val rating: Double, val reviewCount: Int,
                    val distance: Double = 0.0): Parcelable
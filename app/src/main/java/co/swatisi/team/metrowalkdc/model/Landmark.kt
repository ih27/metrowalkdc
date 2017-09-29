package co.swatisi.team.metrowalkdc.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Landmark(val id: String, val name: String, val imageURL: Uri, val displayAddress: String,
                    val lat: Double, val lon: Double, val distance: Double, val rating: Int): Parcelable
package co.swatisi.team.metrowalkdc.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Station(val name: String, val lat: Double, val lon: Double, val lineCode1: String,
                   val lineCode2: String, val lineCode3: String): Parcelable, Comparable<Station> {
    override fun compareTo(other: Station) = name.compareTo(other.name)
}
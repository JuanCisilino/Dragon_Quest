package com.frost.dragonquest.model

import com.google.android.gms.maps.model.LatLng

data class Zone(
    var zone: String,
    var places: ArrayList<Place>
) {

    fun generateLatLongList(): ArrayList<LatLng> {
        val returnList = arrayListOf<LatLng>()
        this.places.forEach {
            val latLng = LatLng(it.latitude, it.longitude)
            returnList.add(latLng)
        }
        return returnList
    }
}

data class Place(
    var id: Int,
    val latitude : Double,
    val longitude : Double
)

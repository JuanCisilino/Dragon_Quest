package com.frost.dragonquest.model

data class Zone(
    var zone: String,
    var places: ArrayList<Place>
)

data class Place(
    var id: Int,
    val latitude : Double,
    val longitude : Double
)

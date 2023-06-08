package com.frost.dragonquest.ui.main

import androidx.lifecycle.ViewModel
import com.frost.dragonquest.model.Zone
import com.google.android.gms.maps.model.LatLng

class MapsViewModel: ViewModel() {

    lateinit var zone: String
    private set

    lateinit var latLngList : ArrayList<LatLng>
    private set

    fun setZone(zona: String?) {
        zone = zona?:"boedo"
    }

    fun setGeofenceList(zone: Zone?){
        latLngList = arrayListOf()
        zone?.let {
            latLngList.clear()
            it.places.forEach { place ->
                latLngList.add(LatLng(place.latitude, place.longitude))
            }
        }
    }

}
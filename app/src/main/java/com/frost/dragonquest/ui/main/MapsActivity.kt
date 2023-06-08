package com.frost.dragonquest.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.frost.dragonquest.R
import com.frost.dragonquest.databinding.ActivityMapsBinding
import com.frost.dragonquest.extensions.*
import com.frost.dragonquest.model.Zone
import com.frost.dragonquest.utils.LoadingDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel>()
    private val loadingDialog = LoadingDialog()
    private val firebaseRemoteConfig = Firebase.remoteConfig
    private var isZoomed = false

    companion object{
        fun start(activity: Activity){
            activity.startActivity(Intent(activity, MapsActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        signOut()
        clearPrefs()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFirebaseRemoteConfig()
        viewModel.setZone(getZonePref())
        setMap()
    }

    private fun setMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun setFirebaseRemoteConfig() {
        loadingDialog.show(supportFragmentManager)
        setMinimalInterval()
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            val exception = task.exception?.message
            when {
                task.isSuccessful -> getRemoteConfig()
                exception != null -> showAlert()
            }
            loadingDialog.dismiss()
        }
        loadingDialog.dismiss()
    }

    private fun setMinimalInterval() {
        val configSettings = buildRemoteConfigSettings()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
    }

    private fun getRemoteConfig(){
        val zones = getZones(firebaseRemoteConfig)
        viewModel.setGeofenceList(zones.find { it.zone == viewModel.zone })
        createAndShowMarkers()
    }

    private fun createAndShowMarkers() {
        val builder = LatLngBounds.Builder()
        buildAndSetMarkers(builder)
        setMarkerClickAnimation(builder)
    }

    private fun setMarkerClickAnimation(builder: LatLngBounds.Builder) {
        val bounds = builder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20))
        mMap.setOnMarkerClickListener {
            isZoomed = if (isZoomed) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20))
                false
            } else {
                it.showInfoWindow()
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it.position, 16F))
                true
            }
            true
        }
    }

    private fun buildAndSetMarkers(builder: LatLngBounds.Builder) {
        viewModel.latLngList.forEach {
            builder.include(it)
            val marker = MarkerOptions().position(it)
            marker.icon(bitmapFromVector(R.drawable.baseline_radio_button_checked_24))
            mMap.addMarker(marker)
        }
    }

}
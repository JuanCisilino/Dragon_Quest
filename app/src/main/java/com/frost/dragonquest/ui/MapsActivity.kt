package com.frost.dragonquest.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.frost.dragonquest.R
import com.frost.dragonquest.databinding.ActivityMapsBinding
import com.frost.dragonquest.extensions.clearPrefs
import com.frost.dragonquest.extensions.getZonePref
import com.frost.dragonquest.extensions.showAlert
import com.frost.dragonquest.extensions.signOut
import com.frost.dragonquest.model.Zone
import com.frost.dragonquest.utils.LoadingDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel>()
    private val loadingDialog = LoadingDialog()
    private val firebaseRemoteConfig = Firebase.remoteConfig
    private val gson = GsonBuilder().create()
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

    //TODO ELIMINAR ESTE METODO CUANDO TERMINE ETAPA DE PRUEBA
    private fun setMinimalInterval() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(5)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
    }

    private fun getRemoteConfig(){
        val lugares = firebaseRemoteConfig.getString("places")
        val zonas : ArrayList<Zone> = gson.fromJson(lugares, object : TypeToken<List<Zone>>() {}.type)
        viewModel.setGeofenceList(zonas.find { it.zone == viewModel.zone })
        createAndShowMarkers()
    }

    private fun createAndShowMarkers() {
        val builder = LatLngBounds.Builder()
        viewModel.latLngList.forEach {
            builder.include(it)
            val marker = MarkerOptions().position(it)
            marker.icon(BitmapFromVector(R.drawable.baseline_stars_24))
            mMap.addMarker(marker)
        }
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

    private fun BitmapFromVector(vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(this, vectorResId)

        // below line is use to set bounds to our vector
        // drawable.
        vectorDrawable!!.setBounds(
            0, 0, vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our
        // bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
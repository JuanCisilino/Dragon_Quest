package com.frost.dragonquest.extensions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.frost.dragonquest.R
import com.frost.dragonquest.model.Zone
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

fun Activity.showAlert(){
    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(R.string.error))
    builder.setMessage(getString(R.string.error_message))
    builder.setPositiveButton("ok", null)
    val dialog = builder.create()
    dialog.show()
}

fun Activity.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.logEventAnalytics(message: String, name:String){
    val analytics = FirebaseAnalytics.getInstance(this)
    val bundle = Bundle()
    bundle.putString("message", message)
    analytics.logEvent(name, bundle)
}

fun Activity.signInWithCredential(credential: AuthCredential) =
    FirebaseAuth.getInstance().signInWithCredential(credential)

fun Activity.signOut() = FirebaseAuth.getInstance().signOut()

fun Activity.getPref(): SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)

fun Activity.savePref(username: String){
    val prefs = getPref().edit()
    prefs.putString(R.string.email.toString(), username)
    prefs.apply()
}

fun Activity.saveZone(zone: String){
    val prefs = getPref().edit()
    prefs.putString(R.string.zone.toString(), zone)
    prefs.apply()
}

fun Activity.getZonePref(): String?{
    val prefs = getPref()
    return prefs.getString(R.string.zone.toString(), null)
}

fun Activity.getEmailPref(): String?{
    val prefs = getPref()
    return prefs.getString(R.string.email.toString(), null)
}

fun Activity.clearPrefs(){
    val prefs = getPref()
    prefs.edit()?.clear()?.apply()
}

fun Activity.buildGoogleConfig() =
    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken(getString(R.string.default_web_client_id))
    .requestEmail()
    .build()

fun Activity.buildRemoteConfigSettings() =
    FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(5)
        .build()

fun Activity.bitmapFromVector(vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(this, vectorResId)
    vectorDrawable!!.setBounds(
        0, 0, vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight)

    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun Activity.getZones(firebaseRemoteConfig: FirebaseRemoteConfig): ArrayList<Zone> =
    GsonBuilder()
        .create()
        .fromJson(firebaseRemoteConfig.getString("places"), object : TypeToken<List<Zone>>() {}.type)
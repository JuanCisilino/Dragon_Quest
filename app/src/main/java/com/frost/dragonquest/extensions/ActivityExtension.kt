package com.frost.dragonquest.extensions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import com.frost.dragonquest.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth

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

fun Activity.getEmailPref(): String?{
    val prefs = getPref()
    return prefs.getString(R.string.email.toString(), null)
}

fun Activity.clearPrefs(){
    val prefs = getPref()
    prefs.edit()?.clear()?.apply()
}
package com.frost.dragonquest.extensions

import android.app.Activity
import android.app.AlertDialog
import com.frost.dragonquest.R

fun Activity.showAlert(){
    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(R.string.error))
    builder.setMessage(getString(R.string.error_message))
    builder.setPositiveButton("ok", null)
    val dialog = builder.create()
    dialog.show()
}
package com.frost.dragonquest.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.frost.dragonquest.databinding.DialogLoadingBinding

class LoadingDialog : DialogFragment() {

    lateinit var dialog: DialogLoadingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        dialog = DialogLoadingBinding.inflate(layoutInflater)
        return dialog.root
    }

    fun show(fragmentManager: FragmentManager){
        show(fragmentManager, null)
    }
}
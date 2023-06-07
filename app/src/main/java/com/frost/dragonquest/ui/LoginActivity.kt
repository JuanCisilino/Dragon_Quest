package com.frost.dragonquest.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.frost.dragonquest.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.googleButton.setOnClickListener { MapsActivity.start(this) }
    }
}
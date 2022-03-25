package com.example.internshipapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.internshipapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.name.animate().translationY(-100f).setDuration(2700).setStartDelay(1000)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, RegisterLogin::class.java)
            startActivity(intent)
            finish()
        }, 3000)


    }
}
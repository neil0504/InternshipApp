package com.example.internshipapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.internshipapp.databinding.ActivityNavigationBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

lateinit var longitude: String
lateinit var latitide: String
const val API_KEY = "AIzaSyBdiL-bvWktAVSnTXio-HRHZLIxlF4u3nM"
lateinit var ccc: Context
class Navigation : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ccc = this
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_navigation)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.clear()
        Toast.makeText(this, "Inside onMapReady Method", Toast.LENGTH_SHORT).show()
        val loc = LatLng(latitide.toDouble(), longitude.toDouble())
        googleMap.addMarker(MarkerOptions()
            .position(loc)
            .title("Current Location")
        )
    }

}
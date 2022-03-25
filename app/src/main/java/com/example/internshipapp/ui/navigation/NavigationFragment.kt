package com.example.internshipapp.ui.navigation

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.internshipapp.CoroutineAsyncTask
import com.example.internshipapp.databinding.FragmentNavigationBinding
import com.example.internshipapp.latitide
import com.example.internshipapp.longitude
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.io.IOException
import java.security.Key


private const val TAG = "NavigationFragment"
private lateinit var cc: Context
class NavigationFragment : Fragment() {

    private var _binding: FragmentNavigationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val LOCATION_PERMISSION_ID = 1000
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var getLoc: GetLoc
    private lateinit var searchView: EditText
    private lateinit var trackBtn: Button
    private lateinit var address: Address
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val dashboardViewModel =
//            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentNavigationBinding.inflate(inflater, container, false)



//        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return binding.root
    }

    private fun searchLocation(){
        val text = searchView.text.toString()
        val geoCoder = Geocoder(cc)
        var arrayList = ArrayList<Address>()
        try{
            arrayList = geoCoder.getFromLocationName(text, 1) as ArrayList<Address>
        }catch (e: IOException){
            Log.d(TAG, "IO Exception")
        }
        if(arrayList.size > 0){
            address = arrayList[0]
            trackBtn.visibility = View.VISIBLE
//            onlocationRecieved(address.latitude.toString(), address.longitude.toString())

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        cc = view.context
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        searchView = binding.search
        trackBtn = binding.track

        getLoc = GetLoc()

        searchView.setOnEditorActionListener { textView, i, keyevent ->
            if(i == EditorInfo.IME_ACTION_SEARCH || i == EditorInfo.IME_ACTION_DONE
                || keyevent.action == KeyEvent.ACTION_DOWN || keyevent.action == KeyEvent.KEYCODE_ENTER){
                searchLocation()
                true
            }else{
                false
            }
        }
        trackBtn.setOnClickListener {
            onlocationRecieved(address.latitude.toString(), address.longitude.toString())
            trackBtn.visibility = View.GONE
        }

        location(context)

    }
    fun location(context: Context) {
        if(checkLocationPermission(context)){
            setLocationRequest()
            getLocation(context)

        }else{
            requestLocationPermissions()
        }
    }

    private fun checkLocationPermission(context: Context): Boolean {
//        isPermissionGranted = false
        val isPermissionGranted = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }else{
            true
        }
        return isPermissionGranted
    }

    private fun setLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 5
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            fastestInterval = 5
            numUpdates = 1
        }
    }
    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_ID
        )
    }

    private fun getLocation(context: Context) {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->


            getLoc.execute(locationRequest)

        }
        val REQUEST_CHECK_SETTINGS = 899

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(context as Activity,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_PERMISSION_ID -> {
                if(grantResults.size==2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    // All permissions granted
                    onLocationGranted()
                }else{
                    onLocationNotGranted()
                }
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 899){
            Log.d(TAG, "Location Services enables by the user")
            getLoc.execute(locationRequest)
        }else{
            Log.d(TAG, "Location Services not enables by the user")
        }
    }

    private fun onLocationGranted(){
        Log.d(TAG, "onRequestPermissionsResult: You have the permission")
        setLocationRequest()
        getLocation(cc)
    }
    fun onLocationNotGranted(){
        Log.d(TAG, "onRequestPermissionsResult: You dont have the permission, therefore displaying dialog")
        // Display Alert Dialog
        val builder = AlertDialog.Builder(cc)
        builder.setMessage("You need to allow app to access location to Function. Allow permission from app settings")
        val dialog = builder.create()
        dialog.show()
    }

    fun onlocationRecieved(latitidee: String, longitudee: String) {
        Toast.makeText(cc, "Location Recieved", Toast.LENGTH_SHORT).show()
        latitide = latitidee
        longitude = longitudee
        val supportMapFragment = childFragmentManager.findFragmentById(binding.maps.id) as SupportMapFragment
        supportMapFragment.getMapAsync(cc as OnMapReadyCallback)
    }
    
    inner class GetLoc: CoroutineAsyncTask<LocationRequest, Void, Array<String>>(){
        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg params: LocationRequest) {
            Log.d(TAG, "doInBackground: Start with treah ID: ${Thread.currentThread().id}")
            var lat = ""
            var lon = ""

            val locationCallback = object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult) {
                    Log.d(TAG, "onLocationResult called, thread ID: ${Thread.currentThread().id}")

                    val lastLocation = locationResult.lastLocation
                    lat = lastLocation.latitude.toString()
                    lon = lastLocation.longitude.toString()
                    onPostExecute(arrayOf(lat, lon))
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(params[0], locationCallback, Looper.getMainLooper())


//            sleep(5001)
            Log.d(TAG, "doInBackground: returning $lat, $lon")
//            return arrayOf<String>(lat, lon)
        }

        override fun onPostExecute(result: Array<String>?) {
            GlobalScope.launch(Dispatchers.Main) {
//                val progressBar: ProgressBar
//                progressBar = (context as Activity).findViewById<ProgressBar>(R.id.)
//                listener.makeProgressBarInvisible()
//                binding.progressBar.visibility = View.INVISIBLE
                Log.d(TAG, "onPostExecute: starts, result = ${result?.get(0)}, ${result?.get(1)}")
                Log.d(TAG, "onPostExecute: starts, thread ID: ${Thread.currentThread().id}")
                val latitide = result?.get(0).toString()
                val longitude = result?.get(1).toString()
//                progressBar.visibility = View.INVISIBLE
                onlocationRecieved(latitide, longitude)

//                binding.LongitudeText.text = longitude
//                binding.latitudeText.text = latitide
//                binding.LongitudeText.visibility = View.VISIBLE
//                binding.latitudeText.visibility = View.VISIBLE
//                val distance = calculateDistance(finalLatitude=finalLatitude.toString(),
//                    finalLongitude = finalLongitude.toString(),
//                    latitude = latitide,
//                    longitude = longitude)
//
//                listener.updateText(latitide, longitude, distance)

            }

        }

        override fun onPreExecute() {
//            listener.makeProgressBarVisible()
//            progressBar.visibility = View.VISIBLE
            super.onPreExecute()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.iko.android.courier.ui.cargo.create

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.iko.android.courier.R

import android.content.pm.ApplicationInfo
import android.location.Address
import android.location.Geocoder
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener

import com.google.android.libraries.places.api.Places
import java.io.IOException
import java.util.*
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_REQUEST_CODE = 1

    private var selectedLocation: LatLng? = null

    private lateinit var addressString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        window.navigationBarColor = resources.getColor(R.color.md_grey_200)
        window.statusBarColor = resources.getColor(R.color.colorStatusBar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // Initializing map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val btnSaveLocation: Button = findViewById(R.id.btnSaveLocation)

        btnSaveLocation.setOnClickListener {
            if (selectedLocation != null) {
                val resultIntent = Intent()
                resultIntent.putExtra("latitude", selectedLocation!!.latitude)
                resultIntent.putExtra("longitude", selectedLocation!!.longitude)
                resultIntent.putExtra("address", addressString)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

        if (!isGpsEnabled()) {
            showGpsPrompt()
        }

    }

    private fun isGpsEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showGpsPrompt() {
        val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }


    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        map.clear()
                        // Delay the initial camera movement
//                        map.setOnMapLoadedCallback {
//                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.0f))
//                        }
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.0f))
//                        map.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                    }
                }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap


        val baku = LatLng(40.394737, 49.6901489)
        // here i want to get current pickUp or deliver location
        val latitude: Double = intent.getDoubleExtra("currentLatitude", -190.0)
        val longitude: Double = intent.getDoubleExtra("currentLongitude", -190.0)
        var savedLocation: LatLng = baku
        if (latitude >= -190.0 && longitude >= -190.0) {
            savedLocation = LatLng(latitude, longitude)

//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(savedLocation, 17F))
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(savedLocation,17F))
        // Delay the camera movement using postDelayed

        // Initially hide the layout
        val btnLayout = findViewById<LinearLayout>(R.id.btn_layout)
        btnLayout.visibility = View.GONE

        map.setOnCameraIdleListener {
            val lat = map.cameraPosition.target.latitude
            val lng = map.cameraPosition.target.longitude
            val addressTV = findViewById<TextView>(R.id.tv)

            // Initializing Geocoder
            val mGeocoder = Geocoder(applicationContext, Locale.getDefault())
            addressString= ""

            // Reverse-Geocoding starts
            try {
                val addressList: MutableList<Address>? = mGeocoder.getFromLocation(lat, lng, 1)

                // use your lat, long value here
                if (addressList != null && addressList.isNotEmpty()) {
                    val address = addressList[0]
                    val sb = StringBuilder()
                    for (i in 0 until address.maxAddressLineIndex) {
                        sb.append(address.getAddressLine(i)).append("\n")
                    }

                    // Various Parameters of an Address are appended
                    // to generate a complete Address
                    if (address.premises != null)
                        sb.append(address.premises).append(", ")
                    if (address.subAdminArea != null)
                        sb.append(address.subAdminArea).append("\n")

                    if (address.locality != null)
                        sb.append(address.locality).append(", ")
                    if (address.adminArea != null)
                        sb.append(address.adminArea).append(", ")
                    if (address.countryName != null)
                        sb.append(address.countryName).append(", ")
                    if (address.postalCode != null)
                        sb.append(address.postalCode)

                    if (sb[sb.length-2] == ',') {
                        sb.deleteCharAt(sb.length-1)
                        sb.deleteCharAt(sb.length-1)
                    } else if(sb[sb.length-1] == '\n')
                        sb.deleteCharAt(sb.length-1)

                    addressString = sb.toString()
                }
            } catch (e: IOException) {
                Toast.makeText(applicationContext,"Unable connect to Geocoder",Toast.LENGTH_LONG).show()
            }

            // Finally, the address string is posted in the textView with LatLng.
            addressTV.text = "Lat: $lat \nLng: $lng \nAddress: $addressString"
            selectedLocation = LatLng(lat, lng)

            // Show the layout when the user has finished interacting with the map
            btnLayout.visibility = View.VISIBLE
        }

        // Enable my location if permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            if (latitude <= -190.0 && longitude <= -190.0) {
                getCurrentLocation()
            }
        }
    }

    fun backToCreateCargo(view: View) {
        this.finish()
    }


}
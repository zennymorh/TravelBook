package com.zennymorh.travelbook

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener(myListener)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                val info = intent.getStringExtra("info")
                if (info == "new") {
                    if (location != null) {
                        mMap.clear()
                        val userLocation = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                    }
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }

        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            locationManager.requestLocationUpdates(LocationManager
                .GPS_PROVIDER, 2, 2f, locationListener)

            val intent = intent
            val info = intent.getStringExtra("info")
            if (info == "new"){
                mMap.clear()
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                    mMap.addMarker(MarkerOptions().position(lastUserLocation).title("Your Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))

            }else {
                mMap.clear()
                val latitude = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)
                val name = intent.getStringExtra("name")
                val location = LatLng(latitude, longitude)

                mMap.addMarker(MarkerOptions().position(location).title(name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

            }
        }
    }

    private val myListener = GoogleMap.OnMapLongClickListener { p0 ->
        mMap.clear()

        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        var address = ""

        if (p0 != null) {
            try {
                val addressList = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)

                if (addressList != null && addressList.size > 0) {
                    if (addressList[0].thoroughfare != null) {
                        address += addressList[0].thoroughfare

                        if (addressList[0].subThoroughfare != null) {
                            address += addressList[0].subThoroughfare
                        }
                    } else {
                        address = "Unnamed Street"
                    }
                } else {
                    address = "New Place"
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            mMap.clear()

            mMap.addMarker(MarkerOptions().position(p0).title(address))

            namesArray.add(address)
            locationArray.add(p0)

            Toast.makeText(applicationContext, "New Place Created", Toast.LENGTH_SHORT).show()

            try {
                val latitude = p0.latitude.toString()
                val longitude = p0.longitude.toString()

                val database = openOrCreateDatabase("places", Context.MODE_PRIVATE, null)

                database.execSQL("CREATE TABLE IF NOT EXISTS places (name VARCHAR, latitude VARCHAR, longitude VARCHAR)")

                val toCompile =
                    "INSERT INTO places (name, latitude, longitude) VALUES (?, ?, ?)"

                val sqLiteStatement = database.compileStatement(toCompile)

                sqLiteStatement.bindString(1, address)
                sqLiteStatement.bindString(2, latitude)
                sqLiteStatement.bindString(3, longitude)

                sqLiteStatement.execute()


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            if (requestCode == 1) {
                if (grantResults.isNotEmpty()) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            2,
                            2f,
                            locationListener
                        )
                    }
                }
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
}
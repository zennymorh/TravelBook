package com.zennymorh.travelbook

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

var namesArray = ArrayList<String>()
var locationArray = ArrayList<LatLng>()

class MainActivity : AppCompatActivity() {

    private val addressAdapter: AddressAdapter by lazy{
        AddressAdapter(namesArray, locationArray, onItemSelected)
    }

    private val onItemSelected by lazy {
        object: ItemClickListener {
            override fun invoke(selectedPlace: String, selectedLatLng: LatLng) {
                val intent = Intent(applicationContext, MapsActivity::class.java)
                intent.putExtra("info", "old")
                intent.putExtra("name", selectedPlace)
                intent.putExtra("latitude", selectedLatLng.latitude)
                intent.putExtra("longitude", selectedLatLng.longitude)
                startActivity(intent)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationList.addItemDecoration(
            DividerItemDecoration(applicationContext,
                DividerItemDecoration.VERTICAL)
        )

        button.setOnClickListener {
            val intent = Intent(applicationContext, MapsActivity::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)
        }

        locationList.adapter = addressAdapter
    }

    override fun onResume() {
        super.onResume()
        try {
            val database = openOrCreateDatabase("places", Context.MODE_PRIVATE, null)
            val cursor = database.rawQuery("SELECT * FROM places", null)

            val nameIndex = cursor.getColumnIndex("name")
            val latitudeIndex = cursor.getColumnIndex("latitude")
            val longitudeIndex = cursor.getColumnIndex("longitude")

            cursor.moveToFirst()

            namesArray.clear()
            locationArray.clear()

            while (cursor != null) {
                val nameFromDatabase = cursor.getString(nameIndex)
                val latitudeFromDatabase = cursor.getString(latitudeIndex)
                val longitudeFromDatabase = cursor.getString(longitudeIndex)

                namesArray.add(nameFromDatabase)

                val latitudeCoordinates = latitudeFromDatabase.toDouble()
                val longitudeCoordinates = longitudeFromDatabase.toDouble()

                val location = LatLng(latitudeCoordinates, longitudeCoordinates)

                locationArray.add(location)

                cursor.moveToNext()
            }

            cursor?.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (namesArray.isEmpty()) {
            add_layout.visibility = View.VISIBLE
        } else {
            add_layout.visibility = View.GONE
        }
        addressAdapter.updateList(namesArray, locationArray)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_place, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_place){
            val intent = Intent(applicationContext, MapsActivity::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }



}

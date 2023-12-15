package com.example.practicegooglemapapplication

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.practicegooglemapapplication.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    //for current location
    lateinit var currentLocation:Location
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient
   private var permissionCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //fusedLocationProvider is used to get device last lcation
        // Initializing fused location client

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        findViewById<FloatingActionButton>(R.id.btnLocation).setOnClickListener(){
           getCurrentLocationUser()
        }


        getCurrentLocationUser()
    }

    private fun getCurrentLocationUser() {
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            !=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this
            ,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this
            , arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),permissionCode)
            return
        }
        val getLocation = fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            location->
            if (location!=null){
                currentLocation = location
                Toast.makeText(this, currentLocation.latitude.toString()+
                        ""+currentLocation.longitude.toString(), Toast.LENGTH_SHORT).show()

                val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)

            }
        }
    }
//press ctrl+O to implement override functcion below fun("onRequestPermissionsResult")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when(requestCode){
        permissionCode->if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            getCurrentLocationUser()
        }
    }

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

        // Add a marker in Sydney and move the camera
        //simple google map
        //val pakistan = LatLng(29.85173801297753, 68.50805964898235)
       // mMap.addMarker(MarkerOptions().position(pakistan).title("Marker in pakistan"))
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(pakistan))
        //map with current location

        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("current location")
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13f))
        googleMap.addMarker(markerOptions)
        //draw circle on map,radius would be in meters
        // for color use android "color" class and also used (R.color.green) etc,
        // strokecolor is used to give border color


        googleMap.addCircle(CircleOptions().center(latLng).radius(1000.0).
        fillColor(Color.GRAY).strokeColor(Color.BLACK))

        //draw polygon on map (polygon mean closed surface whose sides should be >2,and
        // we can draw other shapes like square ,trapezoidal ,triangle etc in polygon
        googleMap.addPolygon(PolygonOptions().add(LatLng(29.85173801297753, 68.50805964898235),
            LatLng(28.85173801297753, 66.50805964898235),
            LatLng(28.85173801297753, 67.50805964898235),
            LatLng(27.85173801297753, 69.50805964898235),
            LatLng(29.85173801297753, 68.50805964898235)).
            fillColor(Color.BLACK).strokeColor(Color.GRAY))
        //to add image on ground level
        googleMap.addGroundOverlay(GroundOverlayOptions()
            .position(latLng,1000f,1000f)
            .image(BitmapDescriptorFactory.
             fromResource(R.drawable.flower)))





    }

    /*this method is used for menu that is using for change map type like satellite,
    terrain ,normal map etc here we attach menu layout */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_menu, menu)
        return true
    }

    //here we perform action on the base of selection type of maps
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Change the map type based on the user's selection.
        return when (item.getItemId()) {
            R.id.normal_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.hybrid_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            R.id.satellite_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }







}
package com.androiddevs.mvvmnewsapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androiddevs.mvvmnewsapp.utils.Constants
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.REQUEST_ENABLE_GPS
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_location.*
import java.util.*

class Splash : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var geoCoder: Geocoder
    var arrayList = mutableListOf<Address>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView((R.layout.activity_location))
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geoCoder = Geocoder(this, Locale.getDefault())
        getLastLocation()
    }

    private fun checkLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) &&
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constants.REQUEST_LOCATION_PERMISSIONS
        )
    }

    private fun getLastLocation() {
        if (checkLocationPermission()) {
            // Now we check  the location service is Enabled
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                    val location = it.result
                    if (location == null) {
                        newLocationData()
                    } else {
                        Log.d("Debug:", "Your Location:" + location.longitude)
                        latitudeTextView.text = location.latitude.toString()
                        longitudeTextView.text = location.longitude.toString()
                        arrayList = geoCoder.getFromLocation(location.latitude,location.longitude,1)
                        addressTextView.text = arrayList[0].countryName

                        // textView.text = "You Current Location is : Long: "+ location.longitude + " , Lat: " + location.latitude + "\n" + getCityName(location.latitude,location.longitude)
                    }
                }
            } else {
                enableGps()
                /* Toast.makeText(this, "Please Turn on Your device Location", Toast.LENGTH_SHORT)
                     .show()*/
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun enableGps() {
        val mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(5000)
            .setFastestInterval(1000)
            .setSmallestDisplacement(27f)


        val settingsBuilder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        settingsBuilder.setAlwaysShow(true)

        val result =
            LocationServices.getSettingsClient(this).checkLocationSettings(settingsBuilder.build())
        result.addOnCompleteListener { task ->

            //getting the status code from exception
            try {
                task.getResult(ApiException::class.java)
            } catch (ex: ApiException) {

                when (ex.statusCode) {

                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {

                       // Toast.makeText(this, "GPS IS OFF", Toast.LENGTH_SHORT).show()

                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        val resolvableApiException = ex as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            this, Constants.REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Toast.makeText(
                            this,
                            "PendingIntent unable to execute request.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Toast.makeText(
                            this,
                            "Something is wrong in your GPS",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun newLocationData() {

        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 3000
        locationRequest.fastestInterval = 1000
        locationRequest.smallestDisplacement = 27f
      //  locationRequest.numUpdates = 2
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallBack, Looper.getMainLooper()
        )
    }


    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(locResult: LocationResult) {
            val lastLocation = locResult.lastLocation

            latitudeTextView.text = lastLocation.latitude.toString()
            longitudeTextView.text = lastLocation.longitude.toString()
            arrayList = geoCoder.getFromLocation(lastLocation.latitude,lastLocation.longitude,1)
            addressTextView.text = arrayList[0].countryName
          /*  Toast.makeText(this@Splash, "Location ${lastLocation.latitude}", Toast.LENGTH_SHORT)
                .show()*/
        }

    }

    // Fun to check if the location service of te=he device is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_LOCATION_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLastLocation()
                } else {
                  //  requestLocationPermission()
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_CHECK_SETTINGS -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        newLocationData()
                        // Toast.makeText(this, "user turned on the GPS ", Toast.LENGTH_SHORT).show()
                    }
                    Activity.RESULT_CANCELED -> {
                        openGpsEnableSetting()
                    }
                }
            }
        }

    }

    private fun openGpsEnableSetting() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, REQUEST_ENABLE_GPS)
    }
}
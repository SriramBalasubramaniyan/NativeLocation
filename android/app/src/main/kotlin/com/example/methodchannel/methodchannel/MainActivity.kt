package com.example.methodchannel.methodchannel

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.methodchannel.methodchannel/native"
    private lateinit var locationManager: LocationManager
    private val PERMISSION_REQUEST_CODE = 1
    private var isLocationRequested = false
    private var locationResult: MethodChannel.Result? = null  // To hold the result for the location request

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        MethodChannel(flutterEngine!!.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "getNativeData") {
                if (!isLocationRequested) {
                    isLocationRequested = true
                    locationResult = result // Store the result to send it later

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        val locationListener = object : LocationListener {
                            override fun onLocationChanged(location: Location) {
                                if (locationResult != null) { // Ensure result is not already sent
                                    locationResult?.success(mapOf("Latitude" to location.latitude, "Longitude" to location.longitude))
                                    locationResult = null  // Reset the result after sending the response
                                }
                            }
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10f, locationListener)
                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
                    }
                } else {
                    result.success(null)
                    isLocationRequested = false
                }
            } else {
                result.notImplemented()
            }
        }
    }
}

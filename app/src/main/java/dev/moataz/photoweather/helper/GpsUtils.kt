package dev.moataz.photoweather.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient


class GpsUtils {

    private var context: Context
    private var onGpsListener: OnGpsListener
    private var mSettingsClient: SettingsClient
    private var mLocationSettingsRequest: LocationSettingsRequest
    private var locationManager: LocationManager
    private var locationRequest: LocationRequest
    private var  fusedLocationProviderClient: FusedLocationProviderClient
    constructor(context: Context, onGpsListener: OnGpsListener, fusedLocationProviderClient: FusedLocationProviderClient) {
        this.context = context
        this.onGpsListener = onGpsListener
        this.fusedLocationProviderClient = fusedLocationProviderClient
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mSettingsClient = LocationServices.getSettingsClient(context)
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5 * 1000.toLong()
        locationRequest.fastestInterval = 5 * 1000.toLong()
        locationRequest.maxWaitTime = 1 * 1000.toLong()


        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        mLocationSettingsRequest = builder.build()
        //**************************
        builder.setAlwaysShow(true) //this is the key ingredient
        //**************************

        Log.d("GpsUtils0", "GpsUtils")

        turnGPSOn()
        detectGPS {  }
    }


    fun onLocationChanged(location: Location) {
        // New location has now been determined
        val msg = "Updated Location: " +
                java.lang.Double.toString(location.latitude) + "," +
                java.lang.Double.toString(location.longitude)
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
//        // You can now create a LatLng Object for use with maps
//        val latLng = LatLng(location.latitude, location.longitude)
        Log.d("onLocationChanged",msg )

    }

    // method for turn on GPS
    fun turnGPSOn() {
        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
            locationManager!!.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)
                ) {

            Log.d("Locationn", "16")
                onGpsListener?.gpsStatus(true)

        } else {
            mSettingsClient?.checkLocationSettings(mLocationSettingsRequest)?.addOnSuccessListener(
                    (context as Activity?)!!
                ) { //  GPS is already enable, callback GPS status through listener
                    onGpsListener?.gpsStatus(true)
                }
                ?.addOnFailureListener(
                    (context as Activity?)!!
                ) { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(
                                context as Activity?,
                                15
                            )
                        } catch (sie: SendIntentException) {
                            Log.i(TAG, "PendingIntent unable to execute request.")
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage =
                                "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings."
                            Log.e(TAG, errorMessage)
                            Toast.makeText(
                                context as Activity?,
                                errorMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
        }



    }


    private var locationCallback: LocationCallback? = null


//    private fun buildLocationRequest(): LocationRequest = LocationRequest.create().apply {
//        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        interval = 5000 //5 seconds
//        fastestInterval = 5000 //5 seconds
//        maxWaitTime = 1000 //1 seconds
//    }

    fun stop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    fun detectGPS(onGPSChanged: (Boolean) -> Unit) {
        locationCallback = object : LocationCallback() {

            override fun onLocationAvailability(var1: LocationAvailability?) {
                Log.i("detectGPS","GPS enabled: ${var1?.isLocationAvailable}")
                onGPSChanged(var1?.isLocationAvailable ?: false)
            }

            override fun onLocationResult(result: LocationResult?) {
                Log.i("detectGPS","New location: ${result?.lastLocation}")
                result?.lastLocation?.let {
                    onGpsListener.gpsLocation(result?.lastLocation)
                }
                stop()
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    interface OnGpsListener {
        fun gpsStatus(isGPSEnable: Boolean)
        fun gpsLocation(location: Location)
    }

}
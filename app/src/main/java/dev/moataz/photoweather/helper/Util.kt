package dev.moataz.photoweather.helper

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import pub.devrel.easypermissions.EasyPermissions

private lateinit var fusedLocationClient: FusedLocationProviderClient


@SuppressLint("MissingPermission")
suspend fun getLocation(context: Context) : Location? {
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    var lastLocation : Location? = null

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location : Location? ->
            lastLocation = location
            Log.d("Locationn", location.toString())

        }

    return lastLocation
}
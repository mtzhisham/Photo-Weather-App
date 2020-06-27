package dev.moataz.photoweather.helper

import android.content.Context
import android.content.SharedPreferences
import hu.autsoft.krate.Krate
import hu.autsoft.krate.floatPref

//I Use krate a wrapper for easy sharedPreferences manipulations
class CustomKrate(private val context: Context) : Krate {

    override val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("krate_prefs", Context.MODE_PRIVATE)
    }


    var lat by floatPref(lat_KEY, defaultValue = 0.0f)
    var lon by floatPref(lon_KEY, defaultValue = 0.0f)


    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        const val lat_KEY = "lat_KEY"
        const val lon_KEY = "lon_KEY"

    }


}
package dev.moataz.photoweather.helper

import android.Manifest

object Constant {
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    const val TAG = "PhotoWeather"
    const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    const val CAMERA_REQUEST_CODE_PERMISSIONS = 10
    val CAMERA_REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)


    const val IMAGE_BUNDLE_KEY = "IMAGE_BUNDLE_KEY"
    const val GALLERY_BUNDLE_KEY = "IMAGE_BUNDLE_KEY"


    const val STORAGE_REQUEST_CODE_PERMISSIONS = 11
    val STORAGE_REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    const val LOCATION_REQUEST_CODE_PERMISSIONS = 12

    const val GPS_REQUEST_CODE_PERMISSIONS = 13

    val LOCATION_REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

}
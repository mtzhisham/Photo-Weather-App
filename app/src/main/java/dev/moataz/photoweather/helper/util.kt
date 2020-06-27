package dev.moataz.photoweather.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.location.Location
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.moataz.photoweather.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

fun getOutputDirectory(fragmentActivity: FragmentActivity, resources: Resources): File {

    val mediaDir = fragmentActivity.externalMediaDirs.firstOrNull()?.let {
        File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else File("")
}

fun listFiles(fragmentActivity: FragmentActivity, resources: Resources) : ArrayList<File>{
  return getOutputDirectory(fragmentActivity, resources).listFiles()?.toCollection(ArrayList<File>())?:arrayListOf()
}
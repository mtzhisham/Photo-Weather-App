package dev.moataz.photoweather.helper

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.net.ConnectivityManager
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import dev.moataz.photoweather.R
import java.io.File


var screenSize: Point? = null
fun getScreenSize(ctx: Context): Point? {

    if (screenSize == null) {
        screenSize = Point()
        val windowManager = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getSize(screenSize)
    }

    return screenSize
}

fun getOutputDirectory(fragmentActivity: FragmentActivity, resources: Resources): File {

    val mediaDir = fragmentActivity.externalMediaDirs.firstOrNull()?.let {
        File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else File("")
}

fun listFiles(fragmentActivity: FragmentActivity, resources: Resources): ArrayList<File> {
    return getOutputDirectory(fragmentActivity, resources).listFiles()
        ?.toCollection(ArrayList<File>()) ?: arrayListOf()
}

/*
* Didn't test the new API after Q for network state yet
* */
@Suppress("DEPRECATION")
fun isNetworkAvailable(context: Context): Boolean {
    val service = Context.CONNECTIVITY_SERVICE
    val manager = context.getSystemService(service) as ConnectivityManager?
    val network = manager?.activeNetworkInfo
    return (network != null)
}

fun getWeatherIconUrl(iconId: String): String {

    return "http://openweathermap.org/img/w/${iconId}.png"
}
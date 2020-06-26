package dev.moataz.photoweather.helper

import android.content.res.Resources
import androidx.fragment.app.FragmentActivity
import dev.moataz.photoweather.R
import java.io.File


fun getOutputDirectory(fragmentActivity: FragmentActivity, resources: Resources): File {

    val mediaDir = fragmentActivity.externalMediaDirs.firstOrNull()?.let {
        File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else File("")
}
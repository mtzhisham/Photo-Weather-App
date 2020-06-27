package dev.moataz.photoweather.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.moataz.photoweather.R
import dev.moataz.photoweather.helper.Constant.GPS_REQUEST_CODE_PERMISSIONS
import dev.moataz.photoweather.helper.Constant.LOCATION_REQUEST_CODE_PERMISSIONS
import dev.moataz.photoweather.helper.Constant.LOCATION_REQUIRED_PERMISSIONS
import dev.moataz.photoweather.helper.CustomKrate
import dev.moataz.photoweather.helper.GPSUtil
import dev.moataz.photoweather.helper.isNetworkAvailable
import dev.moataz.photoweather.viewmodel.WeatherSharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        findNavController(R.id.nav_host_fragment).navigate(item.itemId)
        return super.onOptionsItemSelected(item)
    }
}
package dev.moataz.photoweather

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dev.moataz.photoweather.helper.GpsUtils
import dev.moataz.photoweather.helper.GpsUtils.onGpsListener
import dev.moataz.photoweather.helper.getLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


private const val REQUEST_CODE_PERMISSIONS = 12
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
    )
class MainActivity : AppCompatActivity() {


    private var isGPS  = false


    private val weatherSharedViewModel: WeatherSharedViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        GpsUtils(this).turnGPSOn(object : onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable

                Log.d("Locationn", isGPS.toString())
            }
        })


        // Request camera permissions
        if (allPermissionsGranted()  && isGPS) {
            getLocation()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Requesting read permission",
                REQUEST_CODE_PERMISSIONS,
                *REQUIRED_PERMISSIONS
            )
        }




    }


    private fun allPermissionsGranted() = EasyPermissions.hasPermissions(this@MainActivity, *REQUIRED_PERMISSIONS)


    @AfterPermissionGranted(REQUEST_CODE_PERMISSIONS)
    fun getLocation() {

        if (isGPS)
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("Locationn", getLocation(this@MainActivity).toString())
        }




    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 15) {
                isGPS = true // flag maintain before get location
            }
        }


        weatherSharedViewModel.error.observe(this, Observer {
            it?.let {

                Toast.makeText(this, it,  Toast.LENGTH_SHORT).show()

                weatherSharedViewModel.error.value = null
            }


        })

        weatherSharedViewModel.getCurrentWeather(0.0f,0.0f).observe(this, Observer {

            it?.let {

                Log.d("weatherSharedViewModel", it.toString())

            }

        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


}



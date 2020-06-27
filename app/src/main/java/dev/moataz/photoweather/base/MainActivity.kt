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


    private var isGPS = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val weatherSharedViewModel: WeatherSharedViewModel by viewModel()

    lateinit var GPSUtil: GPSUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)


        // Request gps permissions
        if (allPermissionsGranted()) {

            getLocation()
        } else {
            EasyPermissions.requestPermissions(
                this,
                resources.getString(R.string.location_request_rational),
                LOCATION_REQUEST_CODE_PERMISSIONS,
                *LOCATION_REQUIRED_PERMISSIONS
            )
        }


        //pre-prepare weather details for next screen
        weatherSharedViewModel.mutableCurrentLocation.observe(this, Observer {

            weatherSharedViewModel.error.observe(this, Observer {
                it?.let {

                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()

                    weatherSharedViewModel.error.value = null
                }


            })



                if (isNetworkAvailable(this)) {

                    if (it == null) {
                        weatherSharedViewModel.getCurrentWeather(
                            it.latitude.toFloat(),
                            it.longitude.toFloat()
                        ).observe(this, Observer {

                        })
                    } else{
                        val krate = CustomKrate(this)

                        weatherSharedViewModel.getCurrentWeather(
                            krate.lat,
                            krate.lon
                        ).observe(this, Observer {

                        })

                    }

                } else {

                    Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show()

                }


        })

    }

    private fun allPermissionsGranted() =
        EasyPermissions.hasPermissions(this@MainActivity, *LOCATION_REQUIRED_PERMISSIONS)


    //permission is handled but lint don;t like how i did it
    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(LOCATION_REQUEST_CODE_PERMISSIONS)
    fun getLocation() {
        GPSUtil = GPSUtil(this, object : GPSUtil.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable

            }

            override fun gpsLocation(location: Location) {
                //update the location live data to trigger an API call with the latest location
                weatherSharedViewModel.mutableCurrentLocation.value = location
                val krate = CustomKrate(this@MainActivity)
                krate.lon = location?.longitude.toFloat()?:0.0f
                krate.lat = location?.latitude.toFloat()?:0.0f
            }
        }, fusedLocationClient)

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST_CODE_PERMISSIONS) {
                isGPS = true // flag maintain before get location

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
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
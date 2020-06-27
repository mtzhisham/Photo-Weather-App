package dev.moataz.photoweather

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.moataz.photoweather.helper.CustomKrate
import dev.moataz.photoweather.helper.GpsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


private const val REQUEST_CODE_PERMISSIONS = 12
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
    )
class MainActivity : AppCompatActivity() {


    private var isGPS  = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val weatherSharedViewModel: WeatherSharedViewModel by viewModel()

    lateinit var gpsUtils: GpsUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)



        // Request camera permissions
        if (allPermissionsGranted()) {

            getLocation()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Requesting read permission",
                REQUEST_CODE_PERMISSIONS,
                *REQUIRED_PERMISSIONS
            )
        }




        weatherSharedViewModel.mutableCurrentLocation.observe(this, Observer {



                weatherSharedViewModel.error.observe(this, Observer {
                    it?.let {

                        Toast.makeText(this, it,  Toast.LENGTH_SHORT).show()

                        weatherSharedViewModel.error.value = null
                    }


                })

//            Log.d("Locationn", isGPS.toString() + it.toString())

            it?.let {
                weatherSharedViewModel.getCurrentWeather(it.latitude.toFloat(),it.longitude.toFloat()).observe(this, Observer {

                    it?.let {

                        Log.d("weatherSharedViewModel", it.toString())

                    }

                })

            }





    })








    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }


    private fun allPermissionsGranted() = EasyPermissions.hasPermissions(this@MainActivity, *REQUIRED_PERMISSIONS)


    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE_PERMISSIONS)
    fun getLocation() {

        gpsUtils = GpsUtils(this, object : GpsUtils.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable

                Log.d("Locationn", isGPS.toString() + " 15")

            }
            override fun gpsLocation(location: Location) {
                weatherSharedViewModel.mutableCurrentLocation.value = location
                Log.d("Locationn", isGPS.toString() + " 2" + location)
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
            if (requestCode == 15) {
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


}



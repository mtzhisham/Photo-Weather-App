package dev.moataz.photoweather.ui.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.moataz.photoweather.R
import dev.moataz.photoweather.helper.*
import dev.moataz.photoweather.helper.Constant.IMAGE_BUNDLE_KEY
import dev.moataz.photoweather.viewmodel.WeatherSharedViewModel
import kotlinx.android.synthetic.main.camera_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.lifecycle.Observer


class CameraFragment : Fragment(R.layout.camera_fragment) {


    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var isCameraAvilable = false

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var mContext: Activity


    private var isGPS = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val weatherSharedViewModel: WeatherSharedViewModel by sharedViewModel()

    lateinit var gpsUtil: GPSUtil


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = requireActivity()

        outputDirectory = getOutputDirectory(requireActivity(), resources)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Request camera permissions
        if (CameraPermissionsGranted()) {
            startCamera()
        } else {
            EasyPermissions.requestPermissions(
                this,
                resources.getString(R.string.camera_request_rational),
                Constant.CAMERA_REQUEST_CODE_PERMISSIONS,
                *Constant.CAMERA_REQUIRED_PERMISSIONS
            )
        }

        setUpLocationServices()

        cameraCaptureFab.setOnClickListener {

            // Request camera permissions
            if (CameraPermissionsGranted()) {
               if (!isCameraAvilable) startCamera()


                // Request gps permissions
                if (GPSPermissionsGranted()) {

                    getLocation()
                } else {
                    EasyPermissions.requestPermissions(
                        this,
                        resources.getString(R.string.location_request_rational),
                        Constant.LOCATION_REQUEST_CODE_PERMISSIONS,
                        *Constant.LOCATION_REQUIRED_PERMISSIONS
                    )
                }

                takePhoto()
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    resources.getString(R.string.camera_request_rational),
                    Constant.CAMERA_REQUEST_CODE_PERMISSIONS,
                    *Constant.CAMERA_REQUIRED_PERMISSIONS
                )
            }

            }
    }


    @AfterPermissionGranted(Constant.CAMERA_REQUEST_CODE_PERMISSIONS)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .build()

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera
            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())

                isCameraAvilable = true
            } catch (exc: Exception) {
                isCameraAvilable = false
                Log.e(Constant.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        Log.d(Constant.TAG, "takePhoto")

        val imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                Constant.FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(Constant.TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(Constant.TAG, msg)

                    val bundle = Bundle()
                    bundle.putString(IMAGE_BUNDLE_KEY, photoFile.path)
                    findNavController().navigate(
                        R.id.action_cameraFragment_to_previewFragment,
                        bundle
                    )
                }
            })
    }

    private fun CameraPermissionsGranted() =
        EasyPermissions.hasPermissions(requireContext(), *Constant.CAMERA_REQUIRED_PERMISSIONS)


    private fun GPSPermissionsGranted() =
        EasyPermissions.hasPermissions(requireContext(), *Constant.LOCATION_REQUIRED_PERMISSIONS)


    private fun setUpLocationServices(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Request gps permissions
        if (GPSPermissionsGranted()) {

            getLocation()
        } else {
            EasyPermissions.requestPermissions(
                this,
                resources.getString(R.string.location_request_rational),
                Constant.LOCATION_REQUEST_CODE_PERMISSIONS,
                *Constant.LOCATION_REQUIRED_PERMISSIONS
            )
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



    //permission is handled but lint don't like how i did it
    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(Constant.LOCATION_REQUEST_CODE_PERMISSIONS)
    fun getLocation() {
        gpsUtil = GPSUtil(requireContext(), object : GPSUtil.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable

            }

            override fun gpsLocation(location: Location) {
                //update the location live data to trigger an API call with the latest location
                weatherSharedViewModel.mutableCurrentLocation.value = location
                val krate = CustomKrate(requireContext())
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
            if (requestCode == Constant.GPS_REQUEST_CODE_PERMISSIONS) {
                isGPS = true // flag maintain before get location

            }
        }

    }

    override fun onPause() {
        gpsUtil.stop()
        super.onPause()
    }

    override fun onDestroy() {
        gpsUtil.stop()
        super.onDestroy()
    }

}



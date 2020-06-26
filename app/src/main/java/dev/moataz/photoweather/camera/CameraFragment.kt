package dev.moataz.photoweather.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
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

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dev.moataz.photoweather.R
import dev.moataz.photoweather.helper.getScreenSize
import kotlinx.android.synthetic.main.camera_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment(R.layout.camera_fragment) {

    companion object {
        fun newInstance() = CameraFragment()

        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private lateinit var viewModel: CameraViewModel

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService


    private lateinit var mContext : Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = requireActivity()

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Requesting read permission",
                REQUEST_CODE_PERMISSIONS,
                *REQUIRED_PERMISSIONS
            )
        }

        // Setup the listener for take photo button
        camera_capture_button.setOnClickListener { takePhoto() }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    @AfterPermissionGranted(REQUEST_CODE_PERMISSIONS)
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
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case

        Log.d(TAG, "takePhoto")

        val imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    val bundle = Bundle()
                    bundle.putString("iamge", photoFile.path)
                    findNavController().navigate(R.id.action_cameraFragment_to_previewFragment, bundle)
                }
            })
    }

//    @SuppressLint("UnsafeExperimentalUsageError")
//    private fun savePictureToMemory() {
//
//
//        val imageCapture = imageCapture ?: return
//        imageCapture?.takePicture(ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageCapturedCallback(){
//
//
//            override fun onCaptureSuccess(image: ImageProxy) {
//
//                image.image?.let {
//
//                    val bundle = Bundle()
//
//                    bundle.putParcelable("iamge", it.toBitmap())
//
//                    findNavController().navigate(R.id.action_cameraFragment_to_previewFragment, bundle)
//
//
//
//                }
//
//
//
//                super.onCaptureSuccess(image)
//
//            }
//
//            override fun onError(exception: ImageCaptureException) {
//                super.onError(exception)
//            }
//        })
//    }

    private fun allPermissionsGranted() = EasyPermissions.hasPermissions(requireContext(), *REQUIRED_PERMISSIONS)

    fun getOutputDirectory(): File {

        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else File("")
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

fun Image.toBitmap(): Bitmap {
    val buffer: ByteBuffer = this.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}




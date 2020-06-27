package dev.moataz.photoweather.ui.preview

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import dev.moataz.photoweather.R
import dev.moataz.photoweather.helper.*
import dev.moataz.photoweather.viewmodel.WeatherSharedViewModel
import kotlinx.android.synthetic.main.banner_view.*
import kotlinx.android.synthetic.main.preview_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class PreviewFragment : Fragment(R.layout.preview_fragment) {


    private val viewModel: WeatherSharedViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun allPermissionsGranted() =
        EasyPermissions.hasPermissions(requireContext(), *Constant.STORAGE_REQUIRED_PERMISSIONS)

    var createdBitmap: Bitmap? = null
    var isGalleryPreview = false
    lateinit var file: File

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isNetworkAvailable(requireContext())) {
            bannerMCV.visibility = View.GONE
        }

        //if no arguments is passed no need to do anything in this fragment
        arguments?.let {

            val arg = it

            it.getString(Constant.IMAGE_BUNDLE_KEY)?.let {

                file = File(it)
                arg.getBoolean(Constant.GALLERY_BUNDLE_KEY)?.let {
                    isGalleryPreview = it
                    Log.d("GALLERY_BUNDLE_KEY", it.toString())
                }
                if (!isGalleryPreview) {
                    setUpPhotoAfterCapture()
                } else {
                    setUpPhotoFeomGallery()
                }

            }

        }


        sharePhotoFab.setOnClickListener {

            sharePhoto(file)
        }
    }



    private fun setUpPhotoAfterCapture(){


        Glide.with(previewIV)
            .load(file)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    val builder =
                        resource?.toBitmap()?.let { it1 -> Palette.Builder(it1) }
                    builder?.generate { palette: Palette? ->
                        val vibrantSwatch = palette?.vibrantSwatch
                        previewFragment.setBackgroundColor(
                            (vibrantSwatch?.rgb ?: ContextCompat.getColor(
                                requireContext(),
                                R.color.colorPrimary
                            ))
                        )

                        sharePhotoFab.visibility = View.GONE

                        // Request storage permissions
                        if (allPermissionsGranted()) {

                            previewFragment.getBitmap()?.let {
                                createdBitmap = it
                                saveToDisk()
                            }

                        } else {
                            EasyPermissions.requestPermissions(
                                this@PreviewFragment,
                                resources.getString(R.string.storage_request_rational),
                                Constant.STORAGE_REQUEST_CODE_PERMISSIONS,
                                *Constant.STORAGE_REQUIRED_PERMISSIONS
                            )
                        }

                        sharePhotoFab.visibility = View.VISIBLE

                    }
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }


            })
            .fitCenter()
            .apply(
                RequestOptions().override(
                    getScreenSize(requireContext())?.x ?: 100,
                    getScreenSize(requireContext())?.y ?: 100
                )
            )

            .into(previewIV)



        viewModel.mutableWeatherApiResponse.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {

                it?.let {


                    Glide.with(iconIV)
                        .load(getWeatherIconUrl(it.weather.first().icon))
                        .fitCenter()
                        .into(iconIV)

                    placeTv.text = it.name
                    discTV.text = it.weather.first().description
                    tempTv.text = it.main.temp.toString()

                }

            })





    }


    private fun setUpPhotoFeomGallery(){

        bannerMCV.visibility = View.GONE
        Glide.with(previewIV)
            .load(file)
            .fitCenter()
            .apply(
                RequestOptions().override(
                    getScreenSize(requireContext())?.x ?: 100,
                    getScreenSize(requireContext())?.y ?: 100
                )
            )
            .into(previewIV)

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    private fun sharePhoto(imageFile: File){

        var target = Intent(Intent.ACTION_SEND);
        target.setType("image/*");
        target.putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(
                requireContext(),
                requireContext().getApplicationContext().getPackageName() + ".provider",
                imageFile
            )
        );

        var intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (e: Exception) {

            e.printStackTrace()
        }


    }

    @AfterPermissionGranted(Constant.STORAGE_REQUEST_CODE_PERMISSIONS)
    private fun saveToDisk() {
        createdBitmap?.let {
            var imageFile: File
            if (!isGalleryPreview && file != null) {
                imageFile = File(
                    getOutputDirectory(requireActivity(), resources),
                    SimpleDateFormat(
                        Constant.FILENAME_FORMAT, Locale.US
                    ).format(System.currentTimeMillis()) + ".jpg"
                )
                val os: OutputStream
                try {
                    os = FileOutputStream(imageFile)
                    it.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    os.flush()
                    os.close()

                    file = imageFile
                } catch (e: Exception) {
                    Log.e("test", "Error writing bitmap", e)
                }

            }
        }

    }


}
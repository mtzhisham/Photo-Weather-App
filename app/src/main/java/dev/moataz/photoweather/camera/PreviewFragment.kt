package dev.moataz.photoweather.camera

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
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
import dev.moataz.photoweather.WeatherSharedViewModel
import dev.moataz.photoweather.helper.getOutputDirectory
import dev.moataz.photoweather.helper.getScreenSize
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

    companion object {
        fun newInstance() = PreviewFragment()

        private const val REQUEST_CODE_PERMISSIONS = 11
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private  val viewModel: WeatherSharedViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun allPermissionsGranted() = EasyPermissions.hasPermissions(requireContext(), *REQUIRED_PERMISSIONS)

    var createdBitmap : Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            it.getString("iamge")?.let {
                Glide.with(previewIV)
                    .load(File(it))
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {

                            val builder = resource?.toBitmap()?.let { it1 -> Palette.Builder(it1) }
                           builder?.generate { palette: Palette? ->
                                //access palette instance here
                               val vibrantSwatch = palette?.vibrantSwatch

                               // Set the toolbar background and text colors.
                               // Fall back to default colors if the vibrant swatch is not available.

                               Log.d("color", vibrantSwatch?.rgb.toString())
                               previewFragment.setBackgroundColor(
                                   (vibrantSwatch?.rgb ?:
                                   ContextCompat.getColor(requireContext(), R.color.colorPrimary)))
//                                   setTitleTextColor(vibrantSwatch?.titleTextColor ?:
//                                   ContextCompat.getColor(context, R.color.default_title_color))

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
                    .apply(RequestOptions().override(getScreenSize(requireContext())?.x?:100, getScreenSize(requireContext())?.y?:100))

                    .into(previewIV)




                save_photo_button.setOnClickListener {

                    save_photo_button.visibility = View.GONE



                    // Request camera permissions
                    if (allPermissionsGranted()) {

                        getBitmapFromView(previewFragment)?.let {


                            createdBitmap = it
                            saveToDisk()

                        }


                    } else {
                        EasyPermissions.requestPermissions(
                            this,
                            "Requesting read permission",
                            REQUEST_CODE_PERMISSIONS,
                            *REQUIRED_PERMISSIONS
                        )
                    }



                    save_photo_button.visibility = View.VISIBLE
                }



            }


        }

        viewModel.mutableWeatherApiResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            it?.let {


                Glide.with(iconIV)
                    .load("http://openweathermap.org/img/w/${it.weather.first().icon}.png")
                    .fitCenter()
                    .into(iconIV)



                Log.d("GLIDO", "http://openweathermap.org/img/w/${it.weather.first().icon}.png")
                placeTv.text =  it.name
                discTV.text = it.weather.first().description
                tempTv.text = it.main.temp.toString()



            }

        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }




    @AfterPermissionGranted(REQUEST_CODE_PERMISSIONS)
    private fun saveToDisk() {
        createdBitmap?.let {


            val imageFile =  File(
                getOutputDirectory(requireActivity(), resources),
                SimpleDateFormat(
                    FILENAME_FORMAT, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg")
            val os: OutputStream
            try {
                os = FileOutputStream(imageFile)
                it.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()


                var target = Intent(Intent.ACTION_VIEW);



                target.setData(FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", imageFile));
//                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                var intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (e: Exception) {

                    e.printStackTrace()
                }


            } catch (e: Exception) {
                Log.e("test", "Error writing bitmap", e)
            }

        }


    }



     fun getBitmapFromView(view: View): Bitmap? {
        var bitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
         view.draw(canvas)
         return bitmap
    }

    }
package dev.moataz.photoweather.ui.photogallery

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dev.moataz.photoweather.R
import dev.moataz.photoweather.helper.Constant
import dev.moataz.photoweather.helper.listFiles
import kotlinx.android.synthetic.main.photo_gallery_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class PhotoGalleryFragment : Fragment(R.layout.photo_gallery_fragment),
    IOnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.photoGalleryFragment).setVisible(false);
        super.onPrepareOptionsMenu(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        galleryRv.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)


        if (allPermissionsGranted()) {
            initGalleryRecycler()
        } else {
            EasyPermissions.requestPermissions(
                this,
                resources.getString(R.string.storage_request_rational),
                Constant.STORAGE_REQUEST_CODE_PERMISSIONS,
                *Constant.STORAGE_REQUIRED_PERMISSIONS
            )
        }
    }

    @AfterPermissionGranted(Constant.STORAGE_REQUEST_CODE_PERMISSIONS)
    fun initGalleryRecycler() {
        val adapter = PhotoAdapter(this)
        adapter.submitList(listFiles(requireActivity(), resources))
        galleryRv.setHasFixedSize(true)
        galleryRv.adapter = adapter

    }

    override fun onItemSelected(file: File) {
        val bundle = Bundle()
        bundle.putString(Constant.IMAGE_BUNDLE_KEY, file.path)
        bundle.putBoolean(Constant.GALLERY_BUNDLE_KEY, true)
        findNavController().navigate(R.id.action_photoGalleryFragment_to_previewFragment, bundle)
    }


    private fun allPermissionsGranted() =
        EasyPermissions.hasPermissions(requireContext(), *Constant.STORAGE_REQUIRED_PERMISSIONS)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


}
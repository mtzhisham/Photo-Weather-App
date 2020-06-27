package dev.moataz.photoweather.camera

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dev.moataz.photoweather.R
import dev.moataz.photoweather.helper.listFiles
import kotlinx.android.synthetic.main.photo_gallery_fragment.*
import java.io.File

class PhotoGalleryFragment : Fragment(R.layout.photo_gallery_fragment), IOnItemSelectedListener {

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }

    private lateinit var viewModel: PhotoGalleryViewModel

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


        val adapter = PhotoAdapter(this)
        adapter.submitList(listFiles(requireActivity(), resources))

        galleryRv.adapter = adapter



    }

    override fun onItemSelected(file: File) {
        val bundle = Bundle()
        bundle.putString("iamge", file.path)
        bundle.putBoolean("gallery", true)
        findNavController().navigate(R.id.action_photoGalleryFragment_to_previewFragment, bundle)
    }

}
package dev.moataz.photoweather.camera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.moataz.photoweather.R
import kotlinx.android.synthetic.main.list_item_photo.view.*
import java.io.File


class PhotoAdapter(private val iOnItemSelectedListener: IOnItemSelectedListener) : ListAdapter<File, DoctorViewHolder>(DiffCallback) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_photo, parent, false)

        val layoutParams: ViewGroup.LayoutParams = view.getLayoutParams()
        layoutParams.width = (parent.width * 0.35).toInt()
        layoutParams.height = (parent.height * .35).toInt()

        view.setLayoutParams(layoutParams)

        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {

        holder.bind(getItem(position), iOnItemSelectedListener)
    }
}

object DiffCallback : DiffUtil.ItemCallback<File>() {

    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.absolutePath == newItem.absolutePath
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem == newItem
    }
}


class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(file: File, iOnItemSelectedListener: IOnItemSelectedListener) {


        Glide.with(itemView.photoIv)
            .load(file)
            .thumbnail(.2f)
            .into(itemView.photoIv)

        itemView.setOnClickListener {
            iOnItemSelectedListener.onItemSelected(file)
        }


    }

}

interface IOnItemSelectedListener {
    fun onItemSelected(file: File)

}


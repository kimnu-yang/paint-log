package com.diary.paintlog.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diary.paintlog.data.entities.MyArtWithInfo
import com.diary.paintlog.databinding.ArtworkItemLayoutBinding
import com.diary.paintlog.utils.Common
import com.diary.paintlog.utils.listener.BaseDateListener
import com.diary.paintlog.view.fragments.ArtWorkFragment
import java.time.format.DateTimeFormatter

class ArtWorkAdapter(val listener: BaseDateListener, var data: MutableList<MyArtWithInfo>, var resources: Resources, var context: Context) :
    ListAdapter<ArtWorkFragment.Artwork, ArtWorkAdapter.MyViewHolder>(diffUtil) {

    inner class MyViewHolder(val binding: ArtworkItemLayoutBinding) :
        ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: MyArtWithInfo) {
            val width = resources.displayMetrics.widthPixels * 0.8
            val resourceName = "drawing_${data.art.resourceId.substring(0, 2)}_${data.art.resourceId.substring(2, 4)}"
            val resourceId = resources.getIdentifier(resourceName, "drawable", context.packageName)
            val scaledBitmap = Common.setScaleByWidth(resources, width.toInt(), resourceId)

            binding.artworkImg.setImageBitmap(scaledBitmap)
            binding.artworkTitle.text = data.art.title
            binding.artworkArtist.text = data.art.artist

            val week = Common.getMonthAndWeek(data.myArt.baseDate.toLocalDate())
            val year = data.myArt.baseDate.year.toString()+"년"
            val month = "${"%02d".format(week["month"])}월"
            val weekString = Common.weekNumberToString(week["week"]!!)
            binding.artworkBaseDate.text = "$year $month $weekString"
            binding.artworkDate.text = data.myArt.registeredAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ArtworkItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
        holder.binding.artworkView.setOnClickListener {
            listener.onItemClick(data[position].myArt.baseDate)
        }
    }

    override fun getItemCount() = data.size

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ArtWorkFragment.Artwork>() {
            override fun areItemsTheSame(
                oldItem: ArtWorkFragment.Artwork,
                newItem: ArtWorkFragment.Artwork
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: ArtWorkFragment.Artwork,
                newItem: ArtWorkFragment.Artwork
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}
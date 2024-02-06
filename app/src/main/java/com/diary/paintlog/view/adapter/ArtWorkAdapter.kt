package com.diary.paintlog.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diary.paintlog.databinding.ArtworkItemLayoutBinding
import com.diary.paintlog.view.fragments.ArtWorkFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ArtWorkAdapter(var data: MutableList<ArtWorkFragment.Artwork>) :
    ListAdapter<ArtWorkFragment.Artwork, ArtWorkAdapter.MyViewHolder>(diffUtil) {

    private val TAG = this.javaClass.simpleName

    inner class MyViewHolder(val binding: ArtworkItemLayoutBinding) :
        ViewHolder(binding.root) {
        fun bind(data: ArtWorkFragment.Artwork) {
            binding.artworkTitle.text = data.title
            binding.artworkSummary.text = data.summary
            binding.artworkDate.text = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ArtworkItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.i(TAG, "${position} ${data[position]} ${data.size} ")
        holder.bind(data[position])

        holder.binding.artworkView.setOnClickListener {
            Log.i(TAG, "작품 클릭 [$position] [${data[position].title}] [${data[position].summary}]")
            Log.i(TAG, "${holder.binding.artworkTitle.text}")
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
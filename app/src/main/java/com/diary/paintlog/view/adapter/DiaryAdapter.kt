package com.diary.paintlog.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diary.paintlog.R
import com.diary.paintlog.data.entities.DiaryWithTagAndColor
import com.diary.paintlog.databinding.FragmentDiarySearchItemBinding
import com.diary.paintlog.utils.Common
import java.time.format.DateTimeFormatter
import java.util.Locale

class DiaryAdapter(var data: MutableList<DiaryWithTagAndColor>) :
    RecyclerView.Adapter<DiaryAdapter.MyViewHolder>() {

    private val TAG = this.javaClass.simpleName

    inner class MyViewHolder(val binding: FragmentDiarySearchItemBinding) :
        ViewHolder(binding.root) {
        fun bind(data: DiaryWithTagAndColor) {
            binding.diarySearchListTagLayout.removeAllViews()

            binding.diarySearchListTitle.text = data.diary.title
            binding.diarySearchListContent.text = data.diary.content
            binding.diarySearchListDate.text = data.diary.registeredAt
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd(E)", Locale.KOREA))

            if (data.colors.isNotEmpty()) {
                binding.diarySearchListColor.background =
                    AppCompatResources.getDrawable(binding.root.context, R.drawable.paint_gray)

                Common.imageSetTintWithAlpha(
                    binding.root.context,
                    binding.diarySearchListColor.background,
                    data.colors.first().color.name,
                    data.colors.first().ratio.toString()
                )
            }

            data.tags.forEach { item ->
                addTag(binding, item.tag)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            FragmentDiarySearchItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])

        holder.binding.diarySearchListView.setOnClickListener {
            Log.i(
                TAG,
                "일기 클릭 [$position] [${data[position].diary.id}] [${data[position].diary.title}]"
            )
        }
    }

    override fun getItemCount() = data.size

//    companion object {
//        val diffUtil = object : DiffUtil.ItemCallback<DiaryWithTagAndColor>() {
//            override fun areItemsTheSame(
//                oldItem: DiaryWithTagAndColor,
//                newItem: DiaryWithTagAndColor
//            ): Boolean {
//                Log.i("DIFF1", "${oldItem.diary.id} ${newItem.diary.id}")
//                return oldItem.diary.id == newItem.diary.id
//            }
//
//            override fun areContentsTheSame(
//                oldItem: DiaryWithTagAndColor,
//                newItem: DiaryWithTagAndColor
//            ): Boolean {
//                Log.i("DIFF2", "${oldItem} ${newItem}")
//                return oldItem == newItem
//            }
//        }
//    }

    private fun addTag(binding: FragmentDiarySearchItemBinding, tag: String) {
        // TextView 생성
        val textView = TextView(binding.root.context)
        textView.setBackgroundResource(R.drawable.diary_search_tag_sm_button)
        textView.layoutParams = binding.diarySearchListTagView.layoutParams
        textView.text = tag
        textView.gravity = binding.diarySearchListTagView.gravity
        textView.textSize = 12f
        textView.setOnClickListener {
            Toast.makeText(binding.root.context, "$tag Click!", Toast.LENGTH_SHORT).show()
        }

        binding.diarySearchListTagLayout.addView(textView)
    }
}
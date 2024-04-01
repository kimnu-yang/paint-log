package com.diary.paintlog.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diary.paintlog.R
import com.diary.paintlog.data.entities.DiaryWithTagAndColor
import com.diary.paintlog.databinding.FragmentDiarySearchItemBinding
import com.diary.paintlog.utils.Common
import com.diary.paintlog.utils.listener.DiaryIdListener
import java.time.format.DateTimeFormatter
import java.util.Locale

class DiaryAdapter(private val listener: DiaryIdListener, var data: MutableList<DiaryWithTagAndColor>) :
    RecyclerView.Adapter<DiaryAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: FragmentDiarySearchItemBinding) :
        ViewHolder(binding.root) {
        fun bind(data: DiaryWithTagAndColor) {
            binding.diarySearchListTagLayout.removeAllViews()

            binding.diarySearchListTitle.text = data.diary.title
            binding.diarySearchListContent.text = Common.decrypt(data.diary.content)
            binding.diarySearchListDate.text = data.diary.registeredAt
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd(E)", Locale.KOREA))

            binding.diarySearchListColor.background = null
            binding.diarySearchListColor.background = ContextCompat.getDrawable(binding.root.context, R.drawable.paint_gray)
            if (data.colors.isNotEmpty()) {
                binding.diarySearchListColor.background.setTint(Common.blendColors(binding.root.context, data.colors))
            } else {
                binding.diarySearchListColor.background.setTint(ContextCompat.getColor(binding.root.context, R.color.gray50))
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
            listener.onItemClick(data[position].diary.id)
        }
    }

    override fun getItemCount() = data.size

    private fun addTag(binding: FragmentDiarySearchItemBinding, tag: String) {
        // TextView 생성
        val textView = TextView(binding.root.context)
        textView.setBackgroundResource(R.drawable.diary_search_tag_sm_button)
        textView.layoutParams = binding.diarySearchListTagView.layoutParams
        textView.setPadding(30, 0, 30, 0)
        textView.text = tag
        textView.gravity = binding.diarySearchListTagView.gravity
        textView.textSize = 12f

        binding.diarySearchListTagLayout.addView(textView)
    }
}
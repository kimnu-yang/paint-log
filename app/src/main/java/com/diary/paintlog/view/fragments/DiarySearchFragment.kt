package com.diary.paintlog.view.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diary.paintlog.R
import com.diary.paintlog.data.entities.DiaryTagCount
import com.diary.paintlog.data.entities.DiaryWithTagAndColor
import com.diary.paintlog.databinding.FragmentDiarySearchBinding
import com.diary.paintlog.view.adapter.DiaryAdapter
import com.diary.paintlog.viewmodel.DiarySearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiarySearchFragment : Fragment() {

    val TAG = this.javaClass.simpleName

    private var _binding: FragmentDiarySearchBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 접근용 getter

    private val viewModel: DiarySearchViewModel = DiarySearchViewModel()
    private lateinit var data: MutableList<DiaryWithTagAndColor>
    private lateinit var tagData: List<DiaryTagCount>
    private lateinit var adapter: DiaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiarySearchBinding.inflate(inflater, container, false) // 바인딩 객체 초기화

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            // 좌측 태그 버튼
            data = viewModel.getAllDiaryWithTagAndColor()
            tagData = viewModel.getAllTag()

            Handler(Looper.getMainLooper()).post {
                addTagButton(
                    getString(R.string.diary_search_tag_all),
                    data.size,
                    true
                )
                tagData.forEach { item ->
                    addTagButton(item.tag, item.count)
                }

                // 리스트 목록 세팅
                setListCount()
                adapter = DiaryAdapter(data)

                if (data.size == 0) {
                    binding.diarySearchEmptyView.visibility = View.VISIBLE
                } else {
                    binding.diarySearchEmptyView.visibility = View.GONE

                    binding.diarySearchList.layoutManager = LinearLayoutManager(requireContext())
                    binding.diarySearchList.adapter = adapter

                    binding.diarySearchOrderView.setOnClickListener {
                        data.reverse()
                        adapter.data = data
                        adapter.notifyItemRangeChanged(0, data.size)
                        binding.diarySearchList.scrollToPosition(0)
                    }
                }
            }
        }
    }

    private fun addTagButton(tag: String, size: Int, isAllTag: Boolean = false) {
        val tagMessage = "$tag($size)"

        // TextView 생성
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.bottomMargin = resources.getDimension(R.dimen.margin_medium).toInt()

        val textView = TextView(context)
        textView.layoutParams = layoutParams
        textView.gravity = Gravity.CENTER
        textView.textSize = 16f
        textView.maxLines = 2
        textView.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
        textView.text = tagMessage
        textView.setBackgroundResource(R.drawable.diary_search_tag_button)

        // 처음 기본 선택 처리
        if (isAllTag) {
            textView.setBackgroundResource(R.drawable.diary_search_tag_selected)
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        textView.setOnClickListener {
            binding.diarySearchView.children.iterator().forEach {
                if ((it as TextView).text != tagMessage) {
                    it.setBackgroundResource(R.drawable.diary_search_tag_button)
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.deep))
                } else {
                    it.setBackgroundResource(R.drawable.diary_search_tag_selected)
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
            }

            clickTagButton(tag, isAllTag)
        }

        binding.diarySearchView.addView(textView)
    }

    private fun clickTagButton(tag: String, isAllTag: Boolean = false) {
        val prevDataSize = data.size

        CoroutineScope(Dispatchers.IO).launch {
            data = if (isAllTag) {
                viewModel.getAllDiaryWithTagAndColor()
            } else {
                viewModel.getAllDiaryByTagWithTagAndColor(tag)
            }

            Handler(Looper.getMainLooper()).post {
                setListCount()
                adapter.data.clear()
                adapter.notifyItemRangeRemoved(0, prevDataSize)

                adapter.data = data
                adapter.notifyItemRangeInserted(0, adapter.data.size)
                binding.diarySearchList.scrollToPosition(0)
            }
        }
    }

    private fun setListCount() {
        binding.diarySearchCount.text = getString(R.string.artwork_count, data.size.toString())
    }
}
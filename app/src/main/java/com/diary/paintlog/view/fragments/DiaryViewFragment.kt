package com.diary.paintlog.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.diary.paintlog.R
import com.diary.paintlog.databinding.FragmentDiaryViewBinding
import com.diary.paintlog.utils.Common
import com.diary.paintlog.viewmodel.DiaryColorViewModel
import com.diary.paintlog.viewmodel.DiaryTagViewModel
import com.diary.paintlog.viewmodel.DiaryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaryViewFragment : Fragment() {

    private var _binding: FragmentDiaryViewBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 접근용 getter

    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diaryTagViewModel: DiaryTagViewModel
    private lateinit var diaryColorViewModel: DiaryColorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryViewBinding.inflate(inflater, container, false) // 바인딩 객체 초기화
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]
        diaryTagViewModel = ViewModelProvider(this)[DiaryTagViewModel::class.java]
        diaryColorViewModel = ViewModelProvider(this)[DiaryColorViewModel::class.java]

        val diaryData = arguments
        val diaryId = diaryData?.getLong("diaryId")
        CoroutineScope(Dispatchers.Default).launch {
            if(diaryId != null) {
                val diaryData = diaryViewModel.getDiaryById(diaryId)
                if (diaryData != null) {
                    activity?.runOnUiThread {
                        binding.title.text = diaryData.diary.title
                        binding.content.text = diaryData.diary.content

                        val tag1 = diaryData.tags.filter { it.position == 1 }
                        if(tag1.isNotEmpty()){
                            binding.tag1Text.text = tag1[0].tag
                        } else {
                            binding.tag1.visibility = View.INVISIBLE
                            binding.tag1Text.visibility = View.INVISIBLE
                        }

                        val tag2 = diaryData.tags.filter { it.position == 2 }
                        if(tag2.isNotEmpty()){
                            binding.tag2Text.text = tag2[0].tag
                        } else {
                            binding.tag2.visibility = View.INVISIBLE
                            binding.tag2Text.visibility = View.INVISIBLE
                        }

                        val tag3 = diaryData.tags.filter { it.position == 3 }
                        if(tag3.isNotEmpty()){
                            binding.tag3Text.text = tag3[0].tag
                        } else {
                            binding.tag3.visibility = View.INVISIBLE
                            binding.tag3Text.visibility = View.INVISIBLE
                        }

                        val color1 = diaryData.colors.filter { it.position == 1 }
                        if(color1.isNotEmpty()){
                            binding.color1Text.text = "${color1[0].ratio}%"
                            val drawable = binding.color1.background
                            Common.imageSetTintWithAlpha(requireContext(), drawable, color1[0].color.toString(), color1[0].ratio.toString())
                        } else {
                            binding.color1Text.visibility = View.INVISIBLE
                            binding.color1.visibility = View.INVISIBLE
                        }

                        val color2 = diaryData.colors.filter { it.position == 2 }
                        if(color2.isNotEmpty()){
                            binding.color2Text.text = "${color2[0].ratio}%"
                            val drawable = binding.color2.background
                            Common.imageSetTintWithAlpha(requireContext(), drawable, color2[0].color.toString(), color2[0].ratio.toString())
                        } else {
                            binding.color2Text.visibility = View.INVISIBLE
                            binding.color2.visibility = View.INVISIBLE
                        }

                        val color3 = diaryData.colors.filter { it.position == 3 }
                        if(color3.isNotEmpty()){
                            binding.color3Text.text = "${color3[0].ratio}%"
                            val drawable = binding.color3.background
                            Common.imageSetTintWithAlpha(requireContext(), drawable, color3[0].color.toString(), color3[0].ratio.toString())
                        } else {
                            binding.color3Text.visibility = View.INVISIBLE
                            binding.color3.visibility = View.INVISIBLE
                        }

                        if(diaryData.diary.weather != null){
                            val weatherImage = Common.getWeatherImageByWeather(diaryData.diary.weather)
                            binding.weatherImg.setBackgroundResource(weatherImage)
                            binding.tempMinMax.text = "${diaryData.diary.tempMin}°C / ${diaryData.diary.tempMax}°C"
                            binding.tempNow.text = "${diaryData.diary.tempNow}°C"
                        } else {
                            binding.weatherImg.visibility = View.INVISIBLE
                            binding.tempMinMax.visibility = View.INVISIBLE
                            binding.tempNow.visibility = View.INVISIBLE
                        }

                        binding.saveButton.setOnClickListener {
                            val diaryBundle = Bundle()
                            diaryBundle.putLong("diaryId", diaryData.diary.id)
                            findNavController().navigate(R.id.fragment_diary_update, diaryBundle)
                        }

                        binding.deleteButton.setOnClickListener {
                            showDeleteConfirmDialog(requireContext(), diaryData.diary.id)
                        }
                    }
                }
            } else {
                activity?.runOnUiThread {
                    Common.showToast(requireContext(), "일기 ID 값이 전달되지 않았습니다.")
                    findNavController().navigate(R.id.fragment_main)
                }
            }
        }
    }

    private fun showDeleteConfirmDialog(context: Context, diaryId: Long) {
        diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]
        diaryColorViewModel = ViewModelProvider(this)[DiaryColorViewModel::class.java]
        diaryTagViewModel = ViewModelProvider(this)[DiaryTagViewModel::class.java]

        val builder = AlertDialog.Builder(context)
        builder.setTitle("확인")
        builder.setMessage("일기를 삭제하시겠습니까?")

        builder.setPositiveButton("확인") { _, _ ->
            CoroutineScope(Dispatchers.Default).launch {
                diaryViewModel.deleteDiary(diaryId)
                diaryColorViewModel.deleteDiaryColor(diaryId)
                diaryTagViewModel.deleteDiaryTag(diaryId)

                activity?.runOnUiThread {
                    Common.showToast(context, "일기가 삭제 되었습니다.")
                    findNavController().navigate(R.id.fragment_main)
                }
            }
        }

        // "취소" 버튼 클릭 시 동작 설정
        builder.setNegativeButton("취소") { _, _ ->
        }

        val dialog = builder.create()
        dialog.show()
    }
}
package com.diary.paintlog.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.diary.paintlog.R
import com.diary.paintlog.data.entities.DiaryWithTagAndColor
import com.diary.paintlog.databinding.FragmentWeekDiaryBinding
import com.diary.paintlog.utils.Common
import com.diary.paintlog.view.dialog.DrawingDialog
import com.diary.paintlog.viewmodel.DiaryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WeekDiaryFragment : Fragment() {

    private var _binding: FragmentWeekDiaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var baseDate: LocalDate
    private lateinit var diaryViewModel: DiaryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weekSelect = arguments
        val weekData = weekSelect?.getString("date")?.split("-")

        val date =
            if(weekData != null) LocalDate.of(weekData[0].toInt(),weekData[1].toInt(),weekData[2].toInt())
            else LocalDate.now()
        baseDate = date

        val week = Common.getMonthAndWeek(date)

        val year = date.year.toString()+"년"
        val month = "${"%02d".format(week["month"])}월"
        val weekString = Common.weekNumberToString(week["week"]!!)

        val monday = date.with(DayOfWeek.MONDAY)
        val mondayDate = monday.dayOfMonth
        val tuesdayDate = monday.plusDays(1).dayOfMonth
        val wednesdayDate = monday.plusDays(2).dayOfMonth
        val thursdayDate = monday.plusDays(3).dayOfMonth
        val fridayDate = monday.plusDays(4).dayOfMonth
        val saturdayDate = monday.plusDays(5).dayOfMonth
        val sundayDate = monday.plusDays(6).dayOfMonth

        CoroutineScope(Dispatchers.Default).launch {
            diaryViewModel = ViewModelProvider(this@WeekDiaryFragment)[DiaryViewModel::class.java]

            val weekDiary = diaryViewModel.getDiaryWeek(baseDate)
            val diaryMap = mutableMapOf<String, DiaryWithTagAndColor>()
            var diaryCnt = 0
            for(diary in weekDiary){
                val date = diary.diary.registeredAt.format(DateTimeFormatter.ofPattern("dd"))
                diaryMap[date] = diary
                diaryCnt += 1
            }

            if(diaryCnt > 4){
                binding.drawButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.draw_button_done_selector)
                binding.drawButtonText.text = "이번주 그림 그리기"

                binding.drawButton.setOnClickListener {
                    val dialog = DrawingDialog(baseDate)
                    dialog.show(childFragmentManager, "DrawingDialog")
                }
            } else {
                binding.drawButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.draw_button_yet_selector)
                binding.drawButtonText.text = "물감이 부족해요 $diaryCnt / 5"

                binding.drawButton.setOnClickListener {
                    Common.showToast(requireContext(), "일기를 더 작성해 주세요")
                }
            }

            activity?.runOnUiThread {
                binding.weekText.text = "$year $month $weekString"

                binding.mondayDate.text = "%02d".format(mondayDate)
                if(diaryMap[binding.mondayDate.text] != null) {
                    val diary = diaryMap[binding.mondayDate.text]?.diary
                    if (diary != null) {
                        binding.mondayDiary.visibility = View.VISIBLE
                        binding.mondayDiaryTitle.text = diary.title

                        val diaryBundle = Bundle()
                        diaryBundle.putLong("diaryId", diary.id)
                        binding.mondayDiary.setOnClickListener {
                            findNavController().navigate(R.id.fragment_diary_view, diaryBundle)
                        }
                    }
                    val colors = diaryMap[binding.mondayDate.text]?.colors
                    if (colors!!.isNotEmpty()){
                        val color = Common.blendColors(requireContext(),colors)
                        binding.mondayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_white)
                        binding.mondayPaint.background.setTint(color)
                    } else {
                        binding.mondayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_gray)
                    }
                }

                binding.tuesdayDate.text = "%02d".format(tuesdayDate)
                if(diaryMap[binding.tuesdayDate.text] != null) {
                    val diary = diaryMap[binding.tuesdayDate.text]?.diary
                    if (diary != null) {
                        binding.tuesdayDiary.visibility = View.VISIBLE
                        binding.tuesdayDiaryTitle.text = diary.title

                        val diaryBundle = Bundle()
                        diaryBundle.putLong("diaryId", diary.id)
                        binding.tuesdayDiary.setOnClickListener {
                            findNavController().navigate(R.id.fragment_diary_view, diaryBundle)
                        }
                    }
                    val colors = diaryMap[binding.tuesdayDate.text]?.colors
                    if (colors!!.isNotEmpty()){
                        val color = Common.blendColors(requireContext(),colors)
                        binding.tuesdayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_white)
                        binding.tuesdayPaint.background.setTint(color)
                    } else {
                        binding.tuesdayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_gray)
                    }
                }

                binding.wednesdayDate.text = "%02d".format(wednesdayDate)
                if(diaryMap[binding.wednesdayDate.text] != null) {
                    val diary = diaryMap[binding.wednesdayDate.text]?.diary
                    if (diary != null) {
                        binding.wednesdayDiary.visibility = View.VISIBLE
                        binding.wednesdayDiaryTitle.text = diary.title

                        val diaryBundle = Bundle()
                        diaryBundle.putLong("diaryId", diary.id)
                        binding.wednesdayDiary.setOnClickListener {
                            findNavController().navigate(R.id.fragment_diary_view, diaryBundle)
                        }
                    }
                    val colors = diaryMap[binding.wednesdayDate.text]?.colors
                    if (colors!!.isNotEmpty()){
                        val color = Common.blendColors(requireContext(),colors)
                        binding.wednesdayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_white)
                        binding.wednesdayPaint.background.setTint(color)
                    } else {
                        binding.wednesdayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_gray)
                    }
                }

                binding.thursdayDate.text = "%02d".format(thursdayDate)
                if(diaryMap[binding.thursdayDate.text] != null) {
                    val diary = diaryMap[binding.thursdayDate.text]?.diary
                    if (diary != null) {
                        binding.thursdayDiary.visibility = View.VISIBLE
                        binding.thursdayDiaryTitle.text = diary.title

                        val diaryBundle = Bundle()
                        diaryBundle.putLong("diaryId", diary.id)
                        binding.thursdayDiary.setOnClickListener {
                            findNavController().navigate(R.id.fragment_diary_view, diaryBundle)
                        }
                    }
                    val colors = diaryMap[binding.thursdayDate.text]?.colors
                    if (colors!!.isNotEmpty()){
                        val color = Common.blendColors(requireContext(),colors)
                        binding.thursdayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_white)
                        binding.thursdayPaint.background.setTint(color)
                    } else {
                        binding.thursdayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_gray)
                    }
                }

                binding.fridayDate.text = "%02d".format(fridayDate)
                if(diaryMap[binding.fridayDate.text] != null) {
                    val diary = diaryMap[binding.fridayDate.text]?.diary
                    if (diary != null) {
                        binding.fridayDiary.visibility = View.VISIBLE
                        binding.fridayDiaryTitle.text = diary.title

                        val diaryBundle = Bundle()
                        diaryBundle.putLong("diaryId", diary.id)
                        binding.fridayDiary.setOnClickListener {
                            findNavController().navigate(R.id.fragment_diary_view, diaryBundle)
                        }
                    }
                    val colors = diaryMap[binding.fridayDate.text]?.colors
                    if (colors!!.isNotEmpty()){
                        val color = Common.blendColors(requireContext(),colors)
                        binding.fridayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_white)
                        binding.fridayPaint.background.setTint(color)
                    } else {
                        binding.fridayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_gray)
                    }
                }

                binding.saturdayDate.text = "%02d".format(saturdayDate)
                if(diaryMap[binding.saturdayDate.text] != null) {
                    val diary = diaryMap[binding.saturdayDate.text]?.diary
                    if (diary != null) {
                        binding.saturdayDiary.visibility = View.VISIBLE
                        binding.saturdayDiaryTitle.text = diary.title

                        val diaryBundle = Bundle()
                        diaryBundle.putLong("diaryId", diary.id)
                        binding.saturdayDiary.setOnClickListener {
                            findNavController().navigate(R.id.fragment_diary_view, diaryBundle)
                        }
                    }
                    val colors = diaryMap[binding.saturdayDate.text]?.colors
                    if (colors!!.isNotEmpty()){
                        val color = Common.blendColors(requireContext(),colors)
                        binding.saturdayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_white)
                        binding.saturdayPaint.background.setTint(color)
                    } else {
                        binding.saturdayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_gray)
                    }
                }

                binding.sundayDate.text = "%02d".format(sundayDate)
                if(diaryMap[binding.sundayDate.text] != null) {
                    val diary = diaryMap[binding.sundayDate.text]?.diary
                    if (diary != null) {
                        binding.sundayDiary.visibility = View.VISIBLE
                        binding.sundayDiaryTitle.text = diary.title

                        val diaryBundle = Bundle()
                        diaryBundle.putLong("diaryId", diary.id)
                        binding.sundayDiary.setOnClickListener {
                            findNavController().navigate(R.id.fragment_diary_view, diaryBundle)
                        }
                    }
                    val colors = diaryMap[binding.sundayDate.text]?.colors
                    if (colors!!.isNotEmpty()){
                        val color = Common.blendColors(requireContext(),colors)
                        binding.sundayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_white)
                        binding.sundayPaint.background.setTint(color)
                    } else {
                        binding.sundayPaint.background = ContextCompat.getDrawable(requireContext(), R.drawable.paint_gray)
                    }
                }
            }
        }

        binding.preMonth.setOnClickListener {
            val weekBundle = Bundle()
            weekBundle.putString("date", baseDate.minusWeeks(1).toString())
            findNavController().navigate(R.id.fragment_week_diary, weekBundle)
        }

        binding.nextMonth.setOnClickListener {
            val weekBundle = Bundle()
            weekBundle.putString("date", baseDate.plusWeeks(1).toString())
            findNavController().navigate(R.id.fragment_week_diary, weekBundle)
        }
    }
}
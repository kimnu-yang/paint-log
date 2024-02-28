package com.diary.paintlog.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.diary.paintlog.R
import com.diary.paintlog.data.entities.DiaryWithTagAndColor
import com.diary.paintlog.databinding.FragmentWeekDiaryBinding
import com.diary.paintlog.utils.Common
import com.diary.paintlog.view.dialog.DrawingDialog
import com.diary.paintlog.viewmodel.DiaryViewModel
import com.diary.paintlog.viewmodel.MyArtViewModel
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
    private lateinit var myArtViewModel: MyArtViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "DiscouragedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weekSelect = arguments
        val weekData = weekSelect?.getString("date")?.split("-")

        val pickedDate =
            if(weekData != null) LocalDate.of(weekData[0].toInt(),weekData[1].toInt(),weekData[2].toInt())
            else LocalDate.now()
        baseDate = pickedDate

        val week = Common.getMonthAndWeek(pickedDate)

        val year = pickedDate.year.toString()+"년"
        val month = "${"%02d".format(week["month"])}월"
        val weekString = Common.weekNumberToString(week["week"]!!)

        val monday = pickedDate.with(DayOfWeek.MONDAY)
        val mondayDate = monday.dayOfMonth
        val tuesdayDate = monday.plusDays(1).dayOfMonth
        val wednesdayDate = monday.plusDays(2).dayOfMonth
        val thursdayDate = monday.plusDays(3).dayOfMonth
        val fridayDate = monday.plusDays(4).dayOfMonth
        val saturdayDate = monday.plusDays(5).dayOfMonth
        val sundayDate = monday.plusDays(6).dayOfMonth

        CoroutineScope(Dispatchers.Default).launch {
            diaryViewModel = ViewModelProvider(this@WeekDiaryFragment)[DiaryViewModel::class.java]
            myArtViewModel = ViewModelProvider(this@WeekDiaryFragment)[MyArtViewModel::class.java]

            val weekDiary = diaryViewModel.getDiaryWeek(baseDate)
            val diaryMap = mutableMapOf<String, DiaryWithTagAndColor>()
            var diaryCnt = 0

            var totalPopulation = 0
            var red = 0F
            var orange = 0F
            var yellow = 0F
            var green = 0F
            var blue = 0F
            var navy = 0F
            var violet = 0F

            for(diary in weekDiary){
                if(diary.colors.isNotEmpty()){
                    for(color in diary.colors){
                        when(color.color.toString()){
                            "RED" -> red += color.ratio
                            "ORANGE" -> orange += color.ratio
                            "YELLOW" -> yellow += color.ratio
                            "GREEN" -> green += color.ratio
                            "BLUE" -> blue += color.ratio
                            "NAVY" -> navy += color.ratio
                            "VIOLET" -> violet += color.ratio
                        }
                        totalPopulation += color.ratio
                    }
                }
                val date = diary.diary.registeredAt.format(DateTimeFormatter.ofPattern("dd"))
                diaryMap[date] = diary
                diaryCnt += 1
            }

            if(red > 0.0) red /= totalPopulation
            if(orange > 0.0) orange /= totalPopulation
            if(yellow > 0.0) yellow /= totalPopulation
            if(green > 0.0) green /= totalPopulation
            if(blue > 0.0) blue /= totalPopulation
            if(navy > 0.0) navy /= totalPopulation
            if(violet > 0.0) violet /= totalPopulation

            val myArt = myArtViewModel.getMyArtWeek(baseDate)
            if(myArt != null){
                val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
                val resourceName = "drawing_${myArt.art[0].resourceId.substring(0, 2)}_${myArt.art[0].resourceId.substring(2, 4)}"
                val resourceId = resources.getIdentifier(resourceName, "drawable", requireContext().packageName)
                val scaledBitmap = Common.setScaleByWidth(resources, width, resourceId)

                activity?.runOnUiThread {
                    binding.colorView.visibility = View.VISIBLE
                    binding.colorRedPercent.text = String.format("%.1f", red * 100)+"%"
                    binding.colorOrangePercent.text = String.format("%.1f", orange * 100)+"%"
                    binding.colorYellowPercent.text = String.format("%.1f", yellow * 100)+"%"
                    binding.colorGreenPercent.text = String.format("%.1f", green * 100)+"%"
                    binding.colorBluePercent.text = String.format("%.1f", blue * 100)+"%"
                    binding.colorNavyPercent.text = String.format("%.1f", navy * 100)+"%"
                    binding.colorVioletPercent.text = String.format("%.1f", violet * 100)+"%"

                    val layoutParams = binding.drawingView.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.topToBottom = R.id.color_view

                    binding.weekDrawingTitle.text = "한 주의 그림"
                    binding.drawing.setImageBitmap(scaledBitmap)
                    binding.drawingTitle.text = myArt.art[0].title
                    binding.drawingArtist.text = myArt.art[0].artist
                }
            } else {
                if(diaryCnt > 4){
                    activity?.runOnUiThread {
                        binding.drawButton.visibility = View.VISIBLE
                        binding.drawButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.draw_button_done_selector)
                        binding.drawButtonText.text = "이번주 그림 그리기"
                    }

                    binding.drawButton.setOnClickListener {
                        val dialog = DrawingDialog(baseDate)
                        dialog.show(childFragmentManager, "DrawingDialog")
                    }
                } else {
                    activity?.runOnUiThread {
                        binding.drawButton.visibility = View.VISIBLE
                        binding.drawButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.draw_button_yet_selector)
                        binding.drawButtonText.text = "물감이 부족해요 $diaryCnt / 5"
                    }

                    binding.drawButton.setOnClickListener {
                        Common.showToast(requireContext(), "일기를 더 작성해 주세요")
                    }
                }
            }

            activity?.runOnUiThread {
                binding.weekText.text = "$year $month $weekString"

                binding.mondayDate.text = "%02d".format(mondayDate)
                if(diaryMap[binding.mondayDate.text] != null) {
                    val diary = diaryMap[binding.mondayDate.text]?.diary
                    if (diary != null) {
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
                } else {
                    binding.mondayDiaryTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                    binding.mondayDiaryTitle.text = "미작성"
                    binding.mondayDiary.setOnClickListener {
                        Common.showToast(requireContext(), "작성된 일기가 없습니다.")
                    }
                }

                binding.tuesdayDate.text = "%02d".format(tuesdayDate)
                if(diaryMap[binding.tuesdayDate.text] != null) {
                    val diary = diaryMap[binding.tuesdayDate.text]?.diary
                    if (diary != null) {
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
                } else {
                    binding.tuesdayDiaryTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                    binding.tuesdayDiaryTitle.text = "미작성"
                    binding.tuesdayDiary.setOnClickListener {
                        Common.showToast(requireContext(), "작성된 일기가 없습니다.")
                    }
                }

                binding.wednesdayDate.text = "%02d".format(wednesdayDate)
                if(diaryMap[binding.wednesdayDate.text] != null) {
                    val diary = diaryMap[binding.wednesdayDate.text]?.diary
                    if (diary != null) {
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
                } else {
                    binding.wednesdayDiaryTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                    binding.wednesdayDiaryTitle.text = "미작성"
                    binding.wednesdayDiary.setOnClickListener {
                        Common.showToast(requireContext(), "작성된 일기가 없습니다.")
                    }
                }

                binding.thursdayDate.text = "%02d".format(thursdayDate)
                if(diaryMap[binding.thursdayDate.text] != null) {
                    val diary = diaryMap[binding.thursdayDate.text]?.diary
                    if (diary != null) {
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
                } else {
                    binding.thursdayDiaryTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                    binding.thursdayDiaryTitle.text = "미작성"
                    binding.thursdayDiary.setOnClickListener {
                        Common.showToast(requireContext(), "작성된 일기가 없습니다.")
                    }
                }

                binding.fridayDate.text = "%02d".format(fridayDate)
                if(diaryMap[binding.fridayDate.text] != null) {
                    val diary = diaryMap[binding.fridayDate.text]?.diary
                    if (diary != null) {
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
                } else {
                    binding.fridayDiaryTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                    binding.fridayDiaryTitle.text = "미작성"
                    binding.fridayDiary.setOnClickListener {
                        Common.showToast(requireContext(), "작성된 일기가 없습니다.")
                    }
                }

                binding.saturdayDate.text = "%02d".format(saturdayDate)
                if(diaryMap[binding.saturdayDate.text] != null) {
                    val diary = diaryMap[binding.saturdayDate.text]?.diary
                    if (diary != null) {
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
                } else {
                    binding.saturdayDiaryTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                    binding.saturdayDiaryTitle.text = "미작성"
                    binding.saturdayDiary.setOnClickListener {
                        Common.showToast(requireContext(), "작성된 일기가 없습니다.")
                    }
                }

                binding.sundayDate.text = "%02d".format(sundayDate)
                if(diaryMap[binding.sundayDate.text] != null) {
                    val diary = diaryMap[binding.sundayDate.text]?.diary
                    if (diary != null) {
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
                } else {
                    binding.sundayDiaryTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                    binding.sundayDiaryTitle.text = "미작성"
                    binding.sundayDiary.setOnClickListener {
                        Common.showToast(requireContext(), "작성된 일기가 없습니다.")
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
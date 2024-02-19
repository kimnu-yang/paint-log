package com.diary.paintlog.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.diary.paintlog.R
import com.diary.paintlog.databinding.FragmentWeekDiaryBinding
import com.diary.paintlog.utils.Common
import java.time.DayOfWeek
import java.time.LocalDate

class WeekDiaryFragment : Fragment() {

    private var _binding: FragmentWeekDiaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var baseDate: LocalDate

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

        activity?.runOnUiThread {
            binding.weekText.text = "$year $month $weekString"
            binding.mondayDate.text = "%02d".format(mondayDate)
            binding.tuesdayDate.text = "%02d".format(tuesdayDate)
            binding.wednesdayDate.text = "%02d".format(wednesdayDate)
            binding.thursdayDate.text = "%02d".format(thursdayDate)
            binding.fridayDate.text = "%02d".format(fridayDate)
            binding.saturdayDate.text = "%02d".format(saturdayDate)
            binding.sundayDate.text = "%02d".format(sundayDate)
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
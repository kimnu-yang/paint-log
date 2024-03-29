package com.diary.paintlog.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diary.paintlog.R
import com.diary.paintlog.data.entities.enums.Weather
import com.diary.paintlog.databinding.FragmentStatsBinding
import com.diary.paintlog.utils.Common
import com.diary.paintlog.utils.decorator.CalendarDecorator
import com.diary.paintlog.viewmodel.DiaryViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 접근용 getter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 초기화
        _binding = FragmentStatsBinding.inflate(inflater, container, false) // 바인딩 객체 초기화

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarDecorator = CalendarDecorator(binding.calendarView.context)

        // 좌우 화살표 사이 연, 월의 폰트 스타일 설정
        binding.calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)

        // 요일을 한글로 변경하고 요일별로 색을 바꿈
        binding.calendarView.setWeekDayFormatter(calendarDecorator.CustomWeekDayFormatter())

        // 연월 표기법 변경
        binding.calendarView.setTitleFormatter { day ->
            val inputText = day.date
            val calendarHeaderElements = inputText.toString().split("-")
            val calendarHeaderBuilder = StringBuilder()

            calendarHeaderBuilder.append(calendarHeaderElements[0]).append(" ")
                .append(calendarHeaderElements[1]).append("")

            calendarHeaderBuilder.toString()
        }

        setCalendar(null)
        setColorPercent(null)
        setWeatherPercent(null)

        binding.calendarView.setOnMonthChangedListener { _, date ->
            setCalendar(date)
            setColorPercent(date)
            setWeatherPercent(date)
        }
    }

    private fun setWeatherPercent(selectDate: CalendarDay?) {
        val diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]
        binding.statsWeatherTitle.text = getString(
            R.string.stats_weather_title,
            (selectDate ?: CalendarDay.today()).month.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {

            val weatherCountList = diaryViewModel.getWeatherCount(selectDate ?: CalendarDay.today())

            val weather = HashMap<String, Int>()
            var allCount = 0
            weatherCountList.forEach {
                if (it.weather != null) {
                    weather[it.weather] = it.count
                }

                allCount += it.count
            }

            Weather.entries.forEach {
                var weatherPercentageView: TextView? = null
                var percentage = Double.NaN
                when (it.name) {
                    "SUNNY" -> {
                        weatherPercentageView = binding.weatherSunnyPercent
                        percentage =
                            ((weather["SUNNY"] ?: 0).toDouble() / allCount.toDouble() * 100)
                    }

                    "CLOUDY" -> {
                        weatherPercentageView = binding.weatherCloudyPercent
                        percentage =
                            ((weather["CLOUDY"] ?: 0).toDouble() / allCount.toDouble() * 100)
                    }

                    "OVERCAST" -> {
                        weatherPercentageView = binding.weatherOvercastPercent
                        percentage =
                            ((weather["OVERCAST"] ?: 0).toDouble() / allCount.toDouble() * 100)
                    }

                    "LITTLE_RAIN" -> {
                        weatherPercentageView = binding.weatherLittleRainPercent
                        percentage =
                            ((weather["LITTLE_RAIN"] ?: 0).toDouble() / allCount.toDouble() * 100)
                    }

                    "RAINY" -> {
                        weatherPercentageView = binding.weatherRainyPercent
                        percentage =
                            ((weather["RAINY"] ?: 0).toDouble() / allCount.toDouble() * 100)
                    }

                    "SNOW_RAIN" -> {
                        weatherPercentageView = binding.weatherSnowRainPercent
                        percentage =
                            ((weather["SNOW_RAIN"] ?: 0).toDouble() / allCount.toDouble() * 100)
                    }

                    "SNOWY" -> {
                        weatherPercentageView = binding.weatherSnowyPercent
                        percentage =
                            ((weather["SNOWY"] ?: 0).toDouble() / allCount.toDouble() * 100)
                    }
                }

                if (weatherPercentageView != null) {
                    if (percentage.isNaN()) percentage = 0.0

                    activity?.runOnUiThread {
                        (weatherPercentageView).text = getString(R.string.stats_percent, String.format("%.1f", percentage))

                    }
                }
            }
        }
    }

    private fun setColorPercent(selectDate: CalendarDay?) {
        val diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]
        binding.statsColorTitle.text = getString(
            R.string.stats_color_title,
            (selectDate ?: CalendarDay.today()).month.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            val diaryList = diaryViewModel.getDiaryMonth(selectDate ?: CalendarDay.today())

            var diaryCnt = 0
            var totalPopulation = 0
            var red = 0F
            var orange = 0F
            var yellow = 0F
            var green = 0F
            var blue = 0F
            var navy = 0F
            var violet = 0F

            for (diary in diaryList) {
                if (diary.colors.isNotEmpty()) {
                    for (color in diary.colors) {
                        when (color.color.toString()) {
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
                diaryCnt += 1
            }

            if (red > 0.0) red /= totalPopulation
            if (orange > 0.0) orange /= totalPopulation
            if (yellow > 0.0) yellow /= totalPopulation
            if (green > 0.0) green /= totalPopulation
            if (blue > 0.0) blue /= totalPopulation
            if (navy > 0.0) navy /= totalPopulation
            if (violet > 0.0) violet /= totalPopulation

            binding.colorRedPercent.text =
                getString(R.string.stats_percent, String.format("%.1f", red * 100))
            binding.colorOrangePercent.text =
                getString(R.string.stats_percent, String.format("%.1f", orange * 100))
            binding.colorYellowPercent.text =
                getString(R.string.stats_percent, String.format("%.1f", yellow * 100))
            binding.colorGreenPercent.text =
                getString(R.string.stats_percent, String.format("%.1f", green * 100F))
            binding.colorBluePercent.text =
                getString(R.string.stats_percent, String.format("%.1f", blue * 100F))
            binding.colorNavyPercent.text =
                getString(R.string.stats_percent, String.format("%.1f", navy * 100F))
            binding.colorVioletPercent.text =
                getString(R.string.stats_percent, String.format("%.1f", violet * 100F))
        }
    }

    private fun setCalendar(selectDate: CalendarDay?) {
        val diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]

        // 일자를 꾸며줄 데코레이터 클래스
        val calendarDecorator = CalendarDecorator(binding.calendarView.context)

        // CalendarView의 높이에 맞게 날짜가 출력되도록 설정
        val heightPerWeek = resources.getDimensionPixelSize(R.dimen.calendar_height_per_week)
        binding.calendarView.setTileHeightDp(heightPerWeek)
        binding.calendarView.isDynamicHeightEnabled = true

        val todayDecorator = calendarDecorator.TodayDecorator()
        val saturdayDecorator = calendarDecorator.SaturdayDecorator()
        val sundayDecorator = calendarDecorator.SundayDecorator()

        CoroutineScope(Dispatchers.Default).launch {
            for (data in diaryViewModel.getDiaryMonth(selectDate ?: CalendarDay.today())) {
                val date = data.diary.registeredAt
                val diaryDate = CalendarDay.from(date.year, date.monthValue, date.dayOfMonth)
                val color = if (data.colors.isNotEmpty()) {
                    Common.blendColors(requireContext(), data.colors)
                } else {
                    ContextCompat.getColor(requireContext(), R.color.gray)
                }
                activity?.runOnUiThread {
                    binding.calendarView.addDecorators(
                        calendarDecorator.ColorSpanDecorator(diaryDate, color)
                    )
                }
            }

            if (selectDate != null) {
                val unSelectedDateDecorator = calendarDecorator.UnSelectedDateDecorator(selectDate)
                activity?.runOnUiThread {
                    binding.calendarView.addDecorators(
                        todayDecorator,
                        saturdayDecorator,
                        sundayDecorator,
                        unSelectedDateDecorator,
                    )
                }
            } else {
                binding.calendarView.selectedDate = null
                val unSelectedDateDecorator = calendarDecorator.UnSelectedDateDecorator(null)
                activity?.runOnUiThread {
                    binding.calendarView.addDecorators(
                        todayDecorator,
                        saturdayDecorator,
                        sundayDecorator,
                        unSelectedDateDecorator,
                    )
                }
            }
        }
    }
}
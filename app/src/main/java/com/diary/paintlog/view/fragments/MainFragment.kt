package com.diary.paintlog.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import com.diary.paintlog.R
import com.diary.paintlog.databinding.FragmentMainBinding
import com.diary.paintlog.utils.Common
import com.diary.paintlog.utils.decorator.CalendarDecorator
import com.diary.paintlog.viewmodel.DiaryColorViewModel
import com.diary.paintlog.viewmodel.DiaryTagViewModel
import com.diary.paintlog.viewmodel.DiaryViewModel
import com.diary.paintlog.viewmodel.TopicViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.toHexString
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 접근용 getter
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diaryColorViewModel: DiaryColorViewModel
    private lateinit var diaryTagViewModel: DiaryTagViewModel
    private lateinit var topicViewModel: TopicViewModel

    private var calendarDay: CalendarDay? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 초기화
        _binding = FragmentMainBinding.inflate(inflater, container, false) // 바인딩 객체 초기화
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 원하는 이미지를 리스트에 넣어서 평균 RGB값을 구함
//        getAvgRGB()

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

        // 월 이동 화살표 제거 및 드래그 불가하도록 설정
        binding.calendarView.setRightArrow(0)
        binding.calendarView.setLeftArrow(0)
        binding.calendarView.isPagingEnabled = false

        // 이번달이 아닌 월로 변경되지 않도록 설정
        binding.calendarView.setOnMonthChangedListener { widget, date ->
            if (date.month != CalendarDay.today().month) {
                widget.setCurrentDate(CalendarDay.today(), true)
            }
        }
        setCalendar(null)

        // 초기 추천 주제 셋팅
        CoroutineScope(Dispatchers.Default).launch {
            topicViewModel = ViewModelProvider(this@MainFragment)[TopicViewModel::class.java]
            val topicData = topicViewModel.getRandomTopic()
            activity?.runOnUiThread {
                if (topicData != null) binding.todayTopic.text = topicData.topic
            }
        }

        // 추천 주제를 새로 받아오기
        binding.repeatButton.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                topicViewModel = ViewModelProvider(this@MainFragment)[TopicViewModel::class.java]
                val topicData = topicViewModel.getRandomTopic()
                activity?.runOnUiThread {
                    if (topicData != null) binding.todayTopic.text = topicData.topic
                }
            }
        }

        binding.topicCanvas.setOnClickListener {
            val topicBundle = Bundle()
            topicBundle.putString("topic", binding.todayTopic.text.toString())
            findNavController().navigate(R.id.action_fragment_main_to_fragment_diary, topicBundle)
        }

        // 작성 이벤트 등록
        binding.addDiaryButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_main_to_fragment_diary)
        }

        // 빈 곳 터치시 달력 초기화
        binding.root.setOnClickListener {
            setCalendar(null)

            val today = LocalDate.now()
            val firstDayOfMonth = LocalDate.of(today.year, today.monthValue, 1)
            val firstDayOfCalendar = firstDayOfMonth.with(DayOfWeek.MONDAY)
            activity?.runOnUiThread {
                binding.calendarView.state().edit()
                    .setMinimumDate(firstDayOfCalendar)
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit()

                binding.diaryView.visibility = View.INVISIBLE
                binding.topicView.visibility = View.VISIBLE
                binding.addDiary.visibility = View.VISIBLE
            }
        }

        // 날짜가 바뀔때 일주일 달력으로 변경 및 작성된 일기 출력
        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            if (calendarDay != date || binding.diaryView.visibility == View.INVISIBLE) {
                CoroutineScope(Dispatchers.Default).launch {
                    diaryViewModel =
                        ViewModelProvider(this@MainFragment)[DiaryViewModel::class.java]
                    val selectDate =
                        String.format("%04d-%02d-%02d", date.year, date.month, date.day)
                    val diaryData = diaryViewModel.getDiary(selectDate, "N")

                    if (diaryData != null) {
                        activity?.runOnUiThread {
                            val selectDate = LocalDate.of(date.year, date.month, date.day)
                            val firstDayOfWeek = selectDate.with(DayOfWeek.MONDAY)

                            binding.calendarView.state().edit()
                                .setMinimumDate(firstDayOfWeek)
                                .setCalendarDisplayMode(CalendarMode.WEEKS)
                                .commit()

                            binding.diaryView.visibility = View.VISIBLE
                            binding.topicView.visibility = View.INVISIBLE
                            binding.addDiary.visibility = View.INVISIBLE

                            val constraintLayout =
                                view.findViewById<ConstraintLayout>(R.id.diary_view)
                            constraintLayout.removeAllViews()
                            val inflater = LayoutInflater.from(requireContext())

                            val dynamicLayout = inflater.inflate(
                                R.layout.fragment_diary_view,
                                constraintLayout,
                                false
                            )
                            dynamicLayout.findViewById<TextView>(R.id.title).text =
                                diaryData.diary.title
                            dynamicLayout.findViewById<TextView>(R.id.content).text =
                                Common.decrypt(diaryData.diary.content)

                            val tag1 = diaryData.tags.filter { it.position == 1 }
                            if (tag1.isNotEmpty()) {
                                dynamicLayout.findViewById<TextView>(R.id.tag1_text).text =
                                    tag1[0].tag
                            } else {
                                dynamicLayout.findViewById<ImageView>(R.id.tag1).visibility =
                                    View.INVISIBLE
                                dynamicLayout.findViewById<TextView>(R.id.tag1_text).visibility =
                                    View.INVISIBLE
                            }

                            val tag2 = diaryData.tags.filter { it.position == 2 }
                            if (tag2.isNotEmpty()) {
                                dynamicLayout.findViewById<TextView>(R.id.tag2_text).text =
                                    tag2[0].tag
                            } else {
                                dynamicLayout.findViewById<ImageView>(R.id.tag2).visibility =
                                    View.INVISIBLE
                                dynamicLayout.findViewById<TextView>(R.id.tag2_text).visibility =
                                    View.INVISIBLE
                            }

                            val tag3 = diaryData.tags.filter { it.position == 3 }
                            if (tag3.isNotEmpty()) {
                                dynamicLayout.findViewById<TextView>(R.id.tag3_text).text =
                                    tag3[0].tag
                            } else {
                                dynamicLayout.findViewById<ImageView>(R.id.tag3).visibility =
                                    View.INVISIBLE
                                dynamicLayout.findViewById<TextView>(R.id.tag3_text).visibility =
                                    View.INVISIBLE
                            }

                            val color1 = diaryData.colors.filter { it.position == 1 }
                            if (color1.isNotEmpty()) {
                                dynamicLayout.findViewById<TextView>(R.id.color1_text).text =
                                    "${color1[0].ratio}%"
                                val drawable =
                                    dynamicLayout.findViewById<ImageView>(R.id.color1).background
                                Common.imageSetTintWithAlpha(
                                    requireContext(),
                                    drawable,
                                    color1[0].color.toString(),
                                    color1[0].ratio.toString()
                                )
                            } else {
                                dynamicLayout.findViewById<TextView>(R.id.color1_text).visibility =
                                    View.INVISIBLE
                                dynamicLayout.findViewById<ImageView>(R.id.color1).visibility =
                                    View.INVISIBLE
                            }

                            val color2 = diaryData.colors.filter { it.position == 2 }
                            if (color2.isNotEmpty()) {
                                dynamicLayout.findViewById<TextView>(R.id.color2_text).text =
                                    "${color2[0].ratio}%"
                                val drawable =
                                    dynamicLayout.findViewById<ImageView>(R.id.color2).background
                                Common.imageSetTintWithAlpha(
                                    requireContext(),
                                    drawable,
                                    color2[0].color.toString(),
                                    color2[0].ratio.toString()
                                )
                            } else {
                                dynamicLayout.findViewById<TextView>(R.id.color2_text).visibility =
                                    View.INVISIBLE
                                dynamicLayout.findViewById<ImageView>(R.id.color2).visibility =
                                    View.INVISIBLE
                            }

                            val color3 = diaryData.colors.filter { it.position == 3 }
                            if (color3.isNotEmpty()) {
                                dynamicLayout.findViewById<TextView>(R.id.color3_text).text =
                                    "${color3[0].ratio}%"
                                val drawable =
                                    dynamicLayout.findViewById<ImageView>(R.id.color3).background
                                Common.imageSetTintWithAlpha(
                                    requireContext(),
                                    drawable,
                                    color3[0].color.toString(),
                                    color3[0].ratio.toString()
                                )
                            } else {
                                dynamicLayout.findViewById<TextView>(R.id.color3_text).visibility =
                                    View.INVISIBLE
                                dynamicLayout.findViewById<ImageView>(R.id.color3).visibility =
                                    View.INVISIBLE
                            }

                            if (diaryData.diary.weather != null) {
                                val weatherImage =
                                    Common.getWeatherImageByWeather(diaryData.diary.weather)
                                dynamicLayout.findViewById<ImageView>(R.id.weather_img)
                                    .setBackgroundResource(weatherImage)
                                dynamicLayout.findViewById<TextView>(R.id.temp_min_max).text =
                                    "${diaryData.diary.tempMin}°C / ${diaryData.diary.tempMax}°C"
                                dynamicLayout.findViewById<TextView>(R.id.temp_now).text =
                                    "${diaryData.diary.tempNow}°C"
                            } else {
                                dynamicLayout.findViewById<ImageView>(R.id.weather_img).visibility =
                                    View.INVISIBLE
                                dynamicLayout.findViewById<TextView>(R.id.temp_min_max).visibility =
                                    View.INVISIBLE
                                dynamicLayout.findViewById<TextView>(R.id.temp_now).visibility =
                                    View.INVISIBLE
                            }

                            dynamicLayout.findViewById<ImageButton>(R.id.save_button)
                                .setOnClickListener {
                                    val diaryBundle = Bundle()
                                    diaryBundle.putLong("diaryId", diaryData.diary.id)
                                    findNavController().navigate(
                                        R.id.fragment_diary_update,
                                        diaryBundle
                                    )
                                }

                            dynamicLayout.findViewById<ImageButton>(R.id.delete_button)
                                .setOnClickListener {
                                    showDeleteConfirmDialog(requireContext(), diaryData.diary.id)
                                }

                            val handler = Handler(Looper.getMainLooper())
                            handler.post {
                                constraintLayout.addView(dynamicLayout)
                            }
                        }
                    } else {
                        getBackDiary()
                    }
                }
            } else {
                if (calendarDay?.day == date.day) {
                    getBackDiary()
                }
            }

            calendarDay = date
        }
    }

    private fun getBackDiary() {
        val today = LocalDate.now()
        val firstDayOfMonth = LocalDate.of(today.year, today.monthValue, 1)
        val firstDayOfCalendar = firstDayOfMonth.with(DayOfWeek.MONDAY)
        activity?.runOnUiThread {
            binding.calendarView.state().edit()
                .setMinimumDate(firstDayOfCalendar)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()

            binding.diaryView.visibility = View.INVISIBLE
            binding.topicView.visibility = View.VISIBLE
            binding.addDiary.visibility = View.VISIBLE
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

    private fun setCalendar(selectDate: CalendarDay?) {
        diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]
        diaryColorViewModel = ViewModelProvider(this)[DiaryColorViewModel::class.java]

        // 일자를 꾸며줄 데코레이터 클래스
        val calendarDecorator = CalendarDecorator(binding.calendarView.context)

        // CalendarView의 높이에 맞게 날짜가 출력되도록 설정
        val heightPerWeek = resources.getDimensionPixelSize(R.dimen.calendar_height_per_week)
        binding.calendarView.setTileHeightDp(heightPerWeek)
        binding.calendarView.isDynamicHeightEnabled = true

        CoroutineScope(Dispatchers.Default).launch {
            val todayDecorator = calendarDecorator.TodayDecorator()
            val saturdayDecorator = calendarDecorator.SaturdayDecorator()
            val sundayDecorator = calendarDecorator.SundayDecorator()
            val selectedMonthDecorator =
                calendarDecorator.SelectedMonthDecorator(CalendarDay.today().month)

            for (data in diaryViewModel.getDiaryAll()) {
                val date = data.diary.registeredAt
                val diaryDate = CalendarDay.from(date.year, date.monthValue, date.dayOfMonth)
                val color = if(data.colors.isNotEmpty()){
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
                val selectedDateDecorator = calendarDecorator.SelectedDateDecorator(selectDate)
                val unSelectedDateDecorator = calendarDecorator.UnSelectedDateDecorator(selectDate)
                activity?.runOnUiThread {
                    binding.calendarView.addDecorators(
                        todayDecorator,
                        saturdayDecorator,
                        sundayDecorator,
                        selectedDateDecorator,
                        unSelectedDateDecorator,
                        selectedMonthDecorator,
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
                        selectedMonthDecorator,
                    )
                }
            }
        }
    }

    private fun getAvgRGB(){
        val drawableList = listOf(
            R.drawable.drawing_01_01,
            R.drawable.drawing_01_02,
            R.drawable.drawing_01_03,
            R.drawable.drawing_01_04,
            R.drawable.drawing_01_05,
            R.drawable.drawing_01_06,
            R.drawable.drawing_01_07,
            R.drawable.drawing_01_08,
            R.drawable.drawing_01_09,
            R.drawable.drawing_01_10,
            R.drawable.drawing_02_01,
            R.drawable.drawing_02_02,
            R.drawable.drawing_02_03,
            R.drawable.drawing_03_01,
            R.drawable.drawing_03_02,
            R.drawable.drawing_03_03,
            R.drawable.drawing_03_04,
            R.drawable.drawing_03_05,
            R.drawable.drawing_03_06,
            R.drawable.drawing_03_07,
            R.drawable.drawing_03_08,
            R.drawable.drawing_03_09,
            R.drawable.drawing_03_10,
            R.drawable.drawing_03_11,
            R.drawable.drawing_04_01,
            R.drawable.drawing_04_02,
            R.drawable.drawing_04_03,
            R.drawable.drawing_04_04,
            R.drawable.drawing_04_05,
            R.drawable.drawing_04_06,
            R.drawable.drawing_05_01,
            R.drawable.drawing_05_02,
            R.drawable.drawing_05_03,
            R.drawable.drawing_05_04,
            R.drawable.drawing_05_05,
            R.drawable.drawing_05_06,
            R.drawable.drawing_05_07,
            R.drawable.drawing_06_01,
            R.drawable.drawing_06_02,
            R.drawable.drawing_06_03,
            R.drawable.drawing_06_04,
            R.drawable.drawing_07_01,
            R.drawable.drawing_07_02,
            R.drawable.drawing_07_03,
            R.drawable.drawing_08_01,
            R.drawable.drawing_08_02,
            R.drawable.drawing_08_03,
            R.drawable.drawing_08_04,
            R.drawable.drawing_08_05,
            R.drawable.drawing_08_06,
            R.drawable.drawing_08_07,
            R.drawable.drawing_08_08,
            R.drawable.drawing_09_01,
            R.drawable.drawing_10_01,
            R.drawable.drawing_10_02,
            R.drawable.drawing_10_03,
            R.drawable.drawing_10_04,
            R.drawable.drawing_10_05,
            R.drawable.drawing_10_06,
            R.drawable.drawing_10_07,
            R.drawable.drawing_10_08,
            R.drawable.drawing_10_09,
        )

        for(drawableId in drawableList){
            val drawable = ContextCompat.getDrawable(requireContext(), drawableId)
            val bitmap = (drawable as BitmapDrawable).bitmap
            Palette.from(bitmap).generate { palette ->

                var totalPopulation = 0
                var r = 0F
                var g = 0F
                var b = 0F
                val swatches = palette?.swatches

                swatches?.forEach { swatch ->
                    val color = swatch.rgb.toHexString()
                    val rgb = Common.hexToRgb(color)
                    val population = swatch.population
                    totalPopulation += population

                    r += rgb[0] * population
                    g += rgb[1] * population
                    b += rgb[2] * population
                }

                if (totalPopulation > 0) {
                    val avgR = r / totalPopulation
                    val avgG = g / totalPopulation
                    val avgB = b / totalPopulation

                    val color = Color.rgb(avgR.toInt(),avgG.toInt(),avgB.toInt())
                }
            }
        }
    }
}
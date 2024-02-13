package com.diary.paintlog.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.diary.paintlog.R
import com.diary.paintlog.databinding.FragmentMainBinding
import com.diary.paintlog.utils.decorator.CalendarDecorator
import com.diary.paintlog.viewmodel.DiaryColorViewModel
import com.diary.paintlog.viewmodel.DiaryViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 접근용 getter
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diaryColorViewModel: DiaryColorViewModel

    private var calendarDay: CalendarDay? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 초기화
        _binding = FragmentMainBinding.inflate(inflater, container, false) // 바인딩 객체 초기화
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarDecorator = CalendarDecorator(binding.calendarView.context)

        // 좌우 화살표 사이 연, 월의 폰트 스타일 설정
        binding.calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)

        // 요일을 한글로 변경하고 요일별로 색을 바꿈
        binding.calendarView.setWeekDayFormatter(calendarDecorator.CustomWeekDayFormatter());

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

        // 추천 주제를 새로 받아오기
        binding.repeatButton.setOnClickListener {
            // TODO: DB 연동을 통해 값을 가져오도록 변경
            var text: String = ""
            text =
                if (binding.todayTopic.text == "내가 가장 좋아하는 음악은?") "내가 가장 싫어하는 상황은?"
                else "내가 가장 좋아하는 음악은?"

            binding.todayTopic.text = text
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
            binding.calendarView.state().edit()
                .setMinimumDate(firstDayOfCalendar)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()
        }

        // 날짜가 바뀔때마다 색 지정
        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            if(date != calendarDay)
            {
                setCalendar(date)

                val selectDate = LocalDate.of(date.year,date.month,date.day)
                val firstDayOfWeek = selectDate.with(DayOfWeek.MONDAY)

                binding.calendarView.state().edit()
                    .setMinimumDate(firstDayOfWeek)
                    .setCalendarDisplayMode(CalendarMode.WEEKS)
                    .commit()
            }
        }
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
            val selectedMonthDecorator = calendarDecorator.SelectedMonthDecorator(CalendarDay.today().month)

            val highlightedDate = mutableSetOf<CalendarDay>()
            for(data in diaryViewModel.getDiaryAll()){
                val date = data.diary.registeredAt
                val diaryDate = CalendarDay.from(date.year, date.monthValue, date.dayOfMonth)
                highlightedDate.add(diaryDate)
            }
            val highlightDecorator = calendarDecorator.HighlightDecorator(highlightedDate)

            if(selectDate != null){
                val selectedDateDecorator = calendarDecorator.SelectedDateDecorator(selectDate)
                val unSelectedDateDecorator = calendarDecorator.UnSelectedDateDecorator(selectDate)
                binding.calendarView.addDecorators(
                    todayDecorator,
                    saturdayDecorator,
                    sundayDecorator,
                    selectedDateDecorator,
                    unSelectedDateDecorator,
                    selectedMonthDecorator,
                    highlightDecorator
                )
            } else{
                binding.calendarView.addDecorators(
                    todayDecorator,
                    saturdayDecorator,
                    sundayDecorator,
                    selectedMonthDecorator,
                    highlightDecorator
                )
            }
        }
    }
}
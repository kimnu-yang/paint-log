package com.diary.paintlog.view.activities

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import com.diary.paintlog.R
import com.diary.paintlog.databinding.ActivityMainBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import org.threeten.bp.DayOfWeek

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var logoArrowButton: ImageView
    private val TAG = this.javaClass.simpleName
    private var isImageChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 컨텐츠 설정
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 좌우 화살표 사이 연, 월의 폰트 스타일 설정
        binding.calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)

        // 요일을 한글로 변경하고 요일별로 색을 바꿈
        binding.calendarView.setWeekDayFormatter(CustomWeekDayFormatter());

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

        val saturdayDecorator = SaturdayDecorator(binding.calendarView.context)
        val sundayDecorator = SundayDecorator(binding.calendarView.context)
        val selectedMonthDecorator = SelectedMonthDecorator(CalendarDay.today().month)

        // 특정일 밑에 점찍기
//        val highlightedDate = CalendarDay.from(2024, 1, 24)
//        val highlightDecorator = HighlightDecorator(binding.calendarView.context,setOf(highlightedDate))

        binding.calendarView.addDecorators(saturdayDecorator,sundayDecorator,selectedMonthDecorator)

        // 이번달이 아닌 월로 변경되지 않도록 설정
        binding.calendarView.setOnMonthChangedListener { widget, date ->
            if (date.month != CalendarDay.today().month) {
                widget.setCurrentDate(CalendarDay.today(), true)
            }
        }

        // CalendarView의 높이에 맞게 날짜가 출력되도록 설정
        val heightPerWeek = resources.getDimensionPixelSize(R.dimen.calendar_height_per_week)
        binding.calendarView.setTileHeightDp(heightPerWeek)
        binding.calendarView.isDynamicHeightEnabled = true

        // 날짜가 바뀔때마다 색 지정
        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            val selectedDateDecorator = SelectedDateDecorator(binding.calendarView.context, date)
            val unSelectedDateDecorator = UnSelectedDateDecorator(binding.calendarView.context, date)
            val unSelectedSaturdayDecorator = UnSelectedSaturdayDecorator(binding.calendarView.context, date)
            val unSelectedSundayDecorator = UnSelectedSundayDecorator(binding.calendarView.context, date)
            binding.calendarView.addDecorators(selectedDateDecorator,unSelectedDateDecorator,unSelectedSaturdayDecorator,unSelectedSundayDecorator,selectedMonthDecorator)
        }
    }

    /**
     * 로고를 터치 했을때 메뉴 출력
     */
    fun onLogoGroupClick(view: View) {

        logoArrowButton = findViewById(R.id.logoArrow)

        // 클릭할 때마다 이미지를 변경
        if (isImageChanged) {
            // 이미지를 초기 이미지로 변경
            logoArrowButton.setImageResource(R.drawable.arrow_down)
        } else {
            // 이미지를 변경할 이미지로 변경
            logoArrowButton.setImageResource(R.drawable.arrow_up)

            val popupMenu = PopupMenu(ContextThemeWrapper(this,R.style.PopupMenuCustom), view)
            popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_home -> {
                        // 메뉴 아이템 1 선택 시 할 일
                        // 예: Toast 메시지 표시
                        showToast("Menu Item 1 Clicked")
                        true
                    }
                    R.id.menu_week -> {
                        // 메뉴 아이템 2 선택 시 할 일
                        // 예: Toast 메시지 표시
                        showToast("Menu Item 2 Clicked")
                        true
                    }
                    else -> false
                }
            }
            // 팝업이 닫힐 때의 이벤트 설정
            popupMenu.setOnDismissListener {
                // 팝업이 닫힐 때 이미지, 변수 초기화
                logoArrowButton.setImageResource(R.drawable.arrow_down)
                isImageChanged = !isImageChanged
            }
            popupMenu.show()
        }

        // 이미지 변경 여부를 토글
        isImageChanged = !isImageChanged
    }

    private fun showToast(message: String) {
        // 예시로 Toast 메시지 표시
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * 아래 부터는 Decorator 설정
     */
    /* 토요일 색상을 설정 */
    private class SaturdayDecorator(private val context: Context): DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val dayOfWeek = day.date.dayOfWeek.value // 1 (월요일) ~ 7 (일요일)
            return dayOfWeek == 6 // 토요일
        }

        override fun decorate(view: DayViewFacade) {
            val color = ContextCompat.getColor(context, R.color.blue)
            view.addSpan(ForegroundColorSpan(color))
        }
    }

    /* 일요일 색상을 설정 */
    private class SundayDecorator(private val context: Context): DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val dayOfWeek = day.date.dayOfWeek.value // 1 (월요일) ~ 7 (일요일)
            return dayOfWeek == 7 // 일요일
        }

        override fun decorate(view: DayViewFacade) {
            val color = ContextCompat.getColor(context, R.color.red)
            view.addSpan(ForegroundColorSpan(color))
        }
    }

    /* 이번달에 속하지 않지만 캘린더에 보여지는 이전달/다음달의 일부 날짜를 설정하는 클래스 */
    private inner class SelectedMonthDecorator(val selectedMonth : Int) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.month != selectedMonth
        }
        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(binding.calendarView.context, R.color.gray50)))
        }
    }

    /* 특정 일자 밑에 점을 추가*/
    private class HighlightDecorator(private val context: Context, private val highlightedDays: Set<CalendarDay>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return highlightedDays.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            val color = ContextCompat.getColor(context, R.color.red)
            view.addSpan(DotSpan(5F, color)) // 날짜에 빨간색 동그라미 추가 (이 부분은 원하는 스타일로 수정 가능)
        }
    }

    /* 선택된 일자의 숫자색을 바꿈 */
    private class SelectedDateDecorator(private val context: Context, private val selectedDate: CalendarDay) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == selectedDate
        }

        override fun decorate(view: DayViewFacade) {
            val color = ContextCompat.getColor(context, R.color.white)
            view.addSpan(ForegroundColorSpan(color))
        }

    }

    /* 선택되지 않은 일자의 숫자색을 바꿈 */
    private class UnSelectedDateDecorator(private val context: Context, private val selectedDate: CalendarDay) : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day != selectedDate
        }

        override fun decorate(view: DayViewFacade) {
            val color = ContextCompat.getColor(context, R.color.deep)
            view.addSpan(ForegroundColorSpan(color))
        }
    }

    /* 선택되지 않은 토요일의 색을 바꿈 */
    private class UnSelectedSaturdayDecorator(private val context: Context, private val selectedDate: CalendarDay) : DayViewDecorator {
        private var dayOfWeek = 0
        override fun shouldDecorate(day: CalendarDay): Boolean {
            dayOfWeek = day.date.dayOfWeek.value
            return day != selectedDate && dayOfWeek == 6
        }

        override fun decorate(view: DayViewFacade) {
            val color = ContextCompat.getColor(context, R.color.blue)
            view.addSpan(ForegroundColorSpan(color))
        }
    }

    /* 선택되지 않은 일요일의 색을 바꿈 */
    private class UnSelectedSundayDecorator(private val context: Context, private val selectedDate: CalendarDay) : DayViewDecorator {
        private var dayOfWeek = 0
        override fun shouldDecorate(day: CalendarDay): Boolean {
            dayOfWeek = day.date.dayOfWeek.value
            return day != selectedDate && dayOfWeek == 7
        }

        override fun decorate(view: DayViewFacade) {
            val color = ContextCompat.getColor(context, R.color.red)
            view.addSpan(ForegroundColorSpan(color))
        }
    }

    /* 요일별로 색을 다르게 */
    private inner class CustomWeekDayFormatter : WeekDayFormatter {
        private val saturdayColor = ContextCompat.getColor(binding.calendarView.context, R.color.blue)
        private val sundayColor = ContextCompat.getColor(binding.calendarView.context, R.color.red)

        override fun format(dayOfWeek: DayOfWeek): CharSequence {
            val day = dayOfWeek.value
            val color = when (day) {
                6 -> saturdayColor // 토요일
                7 -> sundayColor   // 일요일
                else -> ContextCompat.getColor(binding.calendarView.context, R.color.deep) // 다른 요일은 검정색
            }

            return SpannableString("${getDayOfWeekName(day)}").apply {
                setSpan(ForegroundColorSpan(color), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        private fun getDayOfWeekName(day: Int): String {
            return when (day) {
                1 -> "월"
                2 -> "화"
                3 -> "수"
                4 -> "목"
                5 -> "금"
                6 -> "토"
                7 -> "일"
                else -> ""
            }
        }
    }
}
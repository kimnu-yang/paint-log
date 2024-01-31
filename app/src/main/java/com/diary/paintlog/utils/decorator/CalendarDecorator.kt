package com.diary.paintlog.utils.decorator

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.diary.paintlog.R
import com.diary.paintlog.utils.common.Common
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import org.threeten.bp.DayOfWeek

class CalendarDecorator(private val context: Context) {

    /* 공통 모듈 준비 */
    private val common = Common()

    /* 오늘 날짜의 색상을 설정 */
    inner class TodayDecorator(): DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == CalendarDay.today()
        }
        override fun decorate(view: DayViewFacade?) {
            context.getDrawable(R.drawable.today_circle)?.let { view?.setBackgroundDrawable(it) }
        }

    }

    /* 토요일 색상을 설정 */
    inner class SaturdayDecorator(): DayViewDecorator {
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
    inner class SundayDecorator(): DayViewDecorator {
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
    inner class SelectedMonthDecorator(private val selectedMonth : Int) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.month != selectedMonth
        }
        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.gray50)))
        }
    }

    /* 특정 일자 밑에 점을 추가*/
    inner class HighlightDecorator(private val highlightedDays: Set<CalendarDay>) :
        DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return highlightedDays.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            val color = ContextCompat.getColor(context, R.color.red)
            view.addSpan(DotSpan(5F, color)) // 날짜에 빨간색 동그라미 추가 (이 부분은 원하는 스타일로 수정 가능)
        }
    }

    /* 선택된 일자의 숫자색을 바꿈 */
    inner class SelectedDateDecorator(private val selectedDate: CalendarDay) :
        DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == selectedDate
        }

        override fun decorate(view: DayViewFacade) {
            val color = ContextCompat.getColor(context, R.color.white)
            view.addSpan(ForegroundColorSpan(color))
        }

    }

    /* 선택되지 않은 일자의 숫자색을 바꿈 */
    inner class UnSelectedDateDecorator(private val selectedDate: CalendarDay) :
        DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day != selectedDate
        }

        override fun decorate(view: DayViewFacade) {
            val color = ContextCompat.getColor(context, R.color.deep)
            view.addSpan(ForegroundColorSpan(color))
        }
    }

    /* 선택되지 않은 토요일의 색을 바꿈 */
    inner class UnSelectedSaturdayDecorator(private val selectedDate: CalendarDay) :
        DayViewDecorator {
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
    inner class UnSelectedSundayDecorator(private val selectedDate: CalendarDay) :
        DayViewDecorator {
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
    inner class CustomWeekDayFormatter : WeekDayFormatter {
        private val saturdayColor = ContextCompat.getColor(context, R.color.blue)
        private val sundayColor = ContextCompat.getColor(context, R.color.red)

        override fun format(dayOfWeek: DayOfWeek): CharSequence {
            val day = dayOfWeek.value
            val color = when (day) {
                6 -> saturdayColor // 토요일
                7 -> sundayColor   // 일요일
                else -> ContextCompat.getColor(context, R.color.deep) // 다른 요일은 검정색
            }

            return SpannableString("${common.getDayOfWeekName(day)}").apply {
                setSpan(ForegroundColorSpan(color), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }


    }
}
package com.diary.paintlog.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.diary.paintlog.R
import com.diary.paintlog.databinding.ActivityMainBinding
import com.diary.paintlog.utils.decorator.CalendarDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var logoArrowButton: ImageView
    private val TAG = this.javaClass.simpleName
    private var isImageChanged = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 컨텐츠 설정
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 일자를 꾸며줄 데코레이터 클래스
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

        val todayDecorator = calendarDecorator.TodayDecorator()
        val saturdayDecorator = calendarDecorator.SaturdayDecorator()
        val sundayDecorator = calendarDecorator.SundayDecorator()
        val selectedMonthDecorator = calendarDecorator.SelectedMonthDecorator(CalendarDay.today().month)

        // 특정일 밑에 점찍기
//        val highlightedDate = CalendarDay.from(2024, 1, 24)
//        val highlightDecorator = HighlightDecorator(binding.calendarView.context,setOf(highlightedDate))

        binding.calendarView.addDecorators(todayDecorator,saturdayDecorator,sundayDecorator,selectedMonthDecorator)

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
        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            val selectedDateDecorator = calendarDecorator.SelectedDateDecorator(date)
            val unSelectedDateDecorator = calendarDecorator.UnSelectedDateDecorator(date)
            val unSelectedSaturdayDecorator = calendarDecorator.UnSelectedSaturdayDecorator(date)
            val unSelectedSundayDecorator = calendarDecorator.UnSelectedSundayDecorator(date)
            binding.calendarView.addDecorators(selectedDateDecorator,unSelectedDateDecorator,unSelectedSaturdayDecorator,unSelectedSundayDecorator,selectedMonthDecorator)
        }

        // 추천 주제를 새로 받아오기
        binding.repeatButton.setOnClickListener {
            // TODO: DB 연동을 통해 값을 가져오도록 변경
            var text: String = ""
            if(binding.todayTopic.text == "내가 가장 좋아하는 음악은?")
                text = "내가 가장 싫어하는 상황은?"
            else
                text = "내가 가장 좋아하는 음악은?"

            binding.todayTopic.text = text
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
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
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

    // Toast 메시지 표시
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // 버튼 클릭 이벤트 처리
    fun goToDiaryActivity(view: View) {
        val intent = Intent(this, DiaryActivity::class.java)
        startActivity(intent)
    }


}
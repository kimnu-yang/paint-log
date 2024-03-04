package com.diary.paintlog.viewmodel

import androidx.lifecycle.ViewModel
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.data.entities.Diary
import com.diary.paintlog.data.entities.DiaryColorCount
import com.diary.paintlog.data.entities.DiaryWeatherCount
import com.diary.paintlog.data.entities.DiaryWithTagAndColor
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DiaryViewModel : ViewModel() {

    private val diaryDao = GlobalApplication.database.diaryDao()
    private val diaryColorDao = GlobalApplication.database.diaryColorDao()

    fun getDiaryAll(): List<DiaryWithTagAndColor> {
        return diaryDao.getAllDiaryWithTagAndColor()
    }

    fun getDiaryWeek(baseDate: LocalDate): List<DiaryWithTagAndColor> {
        val startDate = baseDate.with(DayOfWeek.MONDAY).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val endDate = baseDate.with(DayOfWeek.SUNDAY).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return diaryDao.getWeekDiaryByStartEndDate(startDate, endDate)
    }

    fun getDiaryMonth(date: CalendarDay): List<DiaryWithTagAndColor> {
        val month = "${date.year}-${String.format("%02d", date.month)}%"
        return diaryDao.getMonthDiaryWithTagAndColor(month)
    }

    fun getWeatherCount(date: CalendarDay): List<DiaryWeatherCount> {
        return diaryDao.getWeatherMonthCount("${date.year}-${String.format("%02d", date.month)}%")
    }

    fun getColorCount(date: CalendarDay): List<DiaryColorCount> {
        return diaryColorDao.getColorMonthCount(
            "${date.year}-${
                String.format(
                    "%02d",
                    date.month
                )
            }%"
        )
    }

    fun getDiaryById(diaryId: Long): DiaryWithTagAndColor? {
        return diaryDao.getDiaryWithTagAndColorById(diaryId)
    }

    fun getDiary(date: String, temp: String): DiaryWithTagAndColor? {
        return diaryDao.getDiaryWithTagAndColorByDate(date, temp)
    }

    fun saveDiary(diary: Diary): Long {
        return diaryDao.insertDiary(diary)
    }

    fun updateDiary(diary: Diary) {
        diaryDao.updateDiary(diary)
    }

    fun deleteDiary(diaryId: Long) {
        diaryDao.deleteDiary(diaryId)
    }
}
package com.diary.paintlog.viewmodel

import androidx.lifecycle.ViewModel
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.data.entities.MyArt
import com.diary.paintlog.data.entities.MyArtWithInfo
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyArtViewModel: ViewModel() {

    private val myArtDao = GlobalApplication.database.myArtDao()

    fun getMyArtAll(): List<MyArtWithInfo> {
        return myArtDao.getAllMyArt()
    }

    fun getMyArtWeek(baseDate: LocalDate): MyArtWithInfo? {
        val startDate = baseDate.with(DayOfWeek.MONDAY).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val endDate = baseDate.with(DayOfWeek.SUNDAY).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return myArtDao.getMyArtByBaseDate(startDate, endDate)
    }

    fun saveMyArt(myArt: MyArt) {
        return myArtDao.insertMyArt(myArt)
    }
}
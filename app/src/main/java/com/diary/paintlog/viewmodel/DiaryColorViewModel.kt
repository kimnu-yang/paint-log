package com.diary.paintlog.viewmodel

import androidx.lifecycle.ViewModel
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.data.entities.DiaryColor

class DiaryColorViewModel: ViewModel() {

    private val diaryColorDao = GlobalApplication.database.diaryColorDao()

    fun saveDiaryColor(diaryColor: DiaryColor) {
        return diaryColorDao.insertDiaryColor(diaryColor)
    }

    fun deleteDiaryColor(diaryId: Long){
        diaryColorDao.deleteDiaryColorByDiaryId(diaryId)
    }
}
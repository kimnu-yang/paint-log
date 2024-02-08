package com.diary.paintlog.viewmodel

import androidx.lifecycle.ViewModel
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.data.entities.DiaryColor

class DiaryColorViewModel: ViewModel() {

    private val diaryColorDao = GlobalApplication.database.diaryColorDao()

    fun getDiaryColor(diaryId: Long, position: Int): DiaryColor? {
        return diaryColorDao.getAllDiaryColorByDiaryIdAndPosition(diaryId, position)
    }

    fun saveDiaryColor(diaryColor: DiaryColor) {
        return diaryColorDao.insertDiaryColor(diaryColor)
    }

    fun deleteDiaryColor(diaryId: Long, position: Int) {
        diaryColorDao.deleteDiaryColorByDiaryIdAndPosition(diaryId, position)
    }
}
package com.diary.paintlog.viewmodel

import androidx.lifecycle.ViewModel
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.data.entities.Diary
import com.diary.paintlog.data.entities.DiaryWithTagAndColor

class DiaryViewModel: ViewModel() {

    private val diaryDao = GlobalApplication.database.diaryDao()

    fun getDiaryAll(): List<DiaryWithTagAndColor> {
        return diaryDao.getAllDiaryWithTagAndColor()
    }

    fun getDiary(date: String, temp: String): DiaryWithTagAndColor? {
        return diaryDao.getDiaryWithTagAndColorByDate(date,temp)
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
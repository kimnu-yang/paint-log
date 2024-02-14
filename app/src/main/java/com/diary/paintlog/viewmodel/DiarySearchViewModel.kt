package com.diary.paintlog.viewmodel

import androidx.lifecycle.ViewModel
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.data.entities.DiaryTagCount
import com.diary.paintlog.data.entities.DiaryWithTagAndColor

class DiarySearchViewModel : ViewModel() {
    private val diaryDao = GlobalApplication.database.diaryDao()
    private val diaryTagDao = GlobalApplication.database.diaryTagDao()

    fun getAllDiaryWithTagAndColor(): MutableList<DiaryWithTagAndColor> {
        return diaryDao.getAllDiaryWithTagAndColor().toMutableList()
    }

    fun getAllDiaryByTagWithTagAndColor(tag: String): MutableList<DiaryWithTagAndColor> {
        return diaryDao.getAllDiaryByTagWithTagAndColor(tag).toMutableList()
    }

    fun getAllTag(): List<DiaryTagCount> {
        return diaryTagDao.getAllTag()
    }

}
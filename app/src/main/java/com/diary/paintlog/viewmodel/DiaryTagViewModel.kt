package com.diary.paintlog.viewmodel

import androidx.lifecycle.ViewModel
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.data.entities.DiaryTag

class DiaryTagViewModel: ViewModel() {

    private val diaryTagDao = GlobalApplication.database.diaryTagDao()

    fun saveDiaryTag(diaryTag: DiaryTag) {
        diaryTagDao.insertDiaryTag(diaryTag)
    }

    fun deleteDiaryTag(diaryId: Long) {
        diaryTagDao.deleteDiaryTagByDiaryId(diaryId)
    }
}
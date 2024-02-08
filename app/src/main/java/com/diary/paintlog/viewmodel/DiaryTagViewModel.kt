package com.diary.paintlog.viewmodel

import androidx.lifecycle.ViewModel
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.data.entities.DiaryTag

class DiaryTagViewModel: ViewModel() {

    private val diaryTagDao = GlobalApplication.database.diaryTagDao()

    fun getDiaryTag(diaryId: Long, position: Int): DiaryTag? {
        return diaryTagDao.getAllDiaryTagByDiaryIdAndPosition(diaryId, position)
    }

    fun saveDiaryTag(diaryTag: DiaryTag) {
        diaryTagDao.insertDiaryTag(diaryTag)
    }

    fun deleteDiaryTag(diaryId: Long, position: Int) {
        diaryTagDao.deleteDiaryTagByDiaryIdAndPosition(diaryId, position)
    }
}
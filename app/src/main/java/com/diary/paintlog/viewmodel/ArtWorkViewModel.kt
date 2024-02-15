package com.diary.paintlog.viewmodel

import androidx.lifecycle.ViewModel
import com.diary.paintlog.GlobalApplication

class ArtWorkViewModel : ViewModel() {
    private val diaryDao = GlobalApplication.database.diaryDao()

}
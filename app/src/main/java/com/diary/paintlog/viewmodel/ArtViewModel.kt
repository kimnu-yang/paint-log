package com.diary.paintlog.viewmodel

import androidx.lifecycle.ViewModel
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.data.entities.Art

class ArtViewModel: ViewModel() {

    private val artDao = GlobalApplication.database.artDao()
    fun getAllArt(): List<Art> {
        return artDao.getAllArt()
    }
}
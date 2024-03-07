package com.diary.paintlog.utils.retrofit.model

import com.diary.paintlog.data.entities.DiaryOnlyDate
import com.diary.paintlog.data.entities.MyArt

data class CheckSyncDataRequest(
    private val lastSyncTime: String,
    private val checkList: List<DiaryOnlyDate>,
    private val checkArtList: List<MyArt>
)

package com.diary.paintlog.utils.retrofit.model

import com.diary.paintlog.data.entities.DiaryOnlyDate

data class CheckSyncDataRequest(
    private val lastSyncTime: String,
    private val checkList: List<DiaryOnlyDate>
)

package com.diary.paintlog.utils.retrofit.model

import com.diary.paintlog.data.entities.DiaryWithTagAndColor
import com.google.gson.annotations.SerializedName

data class SyncResponse(
    @SerializedName("result") val result: ApiResult,
    @SerializedName("body") val data: Body
) {
    data class Body(
        @SerializedName("uploadList") val uploadList: List<Long>,
        @SerializedName("downloadList") val downloadList: List<DiaryWithTagAndColor>
    )
}

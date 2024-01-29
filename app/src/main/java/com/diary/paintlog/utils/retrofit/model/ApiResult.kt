package com.diary.paintlog.utils.retrofit.model

import com.google.gson.annotations.SerializedName

data class ApiResult(
    @SerializedName("result_code") val resultCode: Int,
    @SerializedName("result_message") val resultMessage: String,
    @SerializedName("result_description") val resultDescription: String
)

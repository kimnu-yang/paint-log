package com.diary.paintlog.utils.retrofit.model

import com.google.gson.annotations.SerializedName

data class ApiResult(
    @SerializedName("result_code") private val resultCode: String,
    @SerializedName("result_message") private val resultMessage: String,
    @SerializedName("result_description") private val resultDescription: String
)

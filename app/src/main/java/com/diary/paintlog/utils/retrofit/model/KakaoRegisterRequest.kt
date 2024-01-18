package com.example.myapplication.retrofit.model

import com.google.gson.annotations.SerializedName

data class KakaoRegisterRequest(
    @SerializedName("kakao_token") private var kakaoToken: String
)

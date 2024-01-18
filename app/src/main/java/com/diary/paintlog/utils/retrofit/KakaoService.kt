package com.example.myapplication.retrofit

import com.example.myapplication.retrofit.model.ApiRegisterResponse
import com.example.myapplication.retrofit.model.KakaoRegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface KakaoService {
    @POST("open-api/user/kakao/login")
    fun kakaoLogin(@Body request: KakaoRegisterRequest): Call<ApiRegisterResponse>
}
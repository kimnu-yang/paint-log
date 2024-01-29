package com.diary.paintlog.utils.retrofit

import com.diary.paintlog.utils.retrofit.model.ApiLoginResponse
import com.diary.paintlog.utils.retrofit.model.KakaoRegisterRequest
import com.diary.paintlog.utils.retrofit.model.RefreshTokenRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiServerService {
    @POST("open-api/user/kakao/login")
    fun kakaoLogin(@Body request: KakaoRegisterRequest): Call<ApiLoginResponse>

    @POST("open-api/refresh_token")
    fun refreshToken(@Body request: RefreshTokenRequest): Call<ApiLoginResponse>
}
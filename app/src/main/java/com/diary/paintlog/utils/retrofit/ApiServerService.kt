package com.diary.paintlog.utils.retrofit

import com.diary.paintlog.utils.retrofit.model.ApiLoginResponse
import com.diary.paintlog.utils.retrofit.model.KakaoRegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiServerService {
    @POST("open-api/user/kakao/login")
    fun kakaoLogin(@Body request: KakaoRegisterRequest): Call<ApiLoginResponse>

    @DELETE("api/user")
    fun unregistKakaoUser(@Header("authorization-token") kakaoToken: String): Call<ApiLoginResponse>

}
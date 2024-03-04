package com.diary.paintlog.utils.retrofit

import com.diary.paintlog.data.entities.DiaryOnlyDate
import com.diary.paintlog.data.entities.DiaryWithTagAndColor
import com.diary.paintlog.utils.retrofit.model.ApiLoginResponse
import com.diary.paintlog.utils.retrofit.model.KakaoRegisterRequest
import com.diary.paintlog.utils.retrofit.model.SyncResponse
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

    @POST("api/setting/check")
    fun syncDataCheck(
        @Header("authorization-token") kakaoToken: String,
        @Body data: List<DiaryOnlyDate>
    ): Call<SyncResponse>

    @POST("api/setting/upload")
    fun uploadDiary(
        @Header("authorization-token") kakaoToken: String,
        @Body data: List<DiaryWithTagAndColor>
    ): Call<Any>
}
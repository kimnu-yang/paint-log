package com.diary.paintlog.utils.retrofit

import com.diary.paintlog.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServerClient {
//    private val okHttpClient = OkHttpClient.Builder()
//        .dispatcher(Dispatcher().apply {
//            maxRequests = 1 // 동시에 처리할 최대 요청 수를 1로 설정하여 한 번에 하나의 요청만 처리
//        })
//        .build()

    var api: ApiServerService = Retrofit.Builder()
        .baseUrl(BuildConfig.API_SERVER_ADDRESS)
        .addConverterFactory(GsonConverterFactory.create())
//        .client(okHttpClient)
        .build()
        .create(ApiServerService::class.java)
}
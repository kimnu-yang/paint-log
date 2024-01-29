package com.diary.paintlog.utils.retrofit

import com.diary.paintlog.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServerClient {
    var api: ApiServerService = Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER_ADDRESS)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiServerService::class.java)
}
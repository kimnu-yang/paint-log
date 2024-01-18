package com.diary.paintlog.utils.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServerClient {
    var api: ApiServerService = Retrofit.Builder()
        .baseUrl("http://222.235.2.221:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiServerService::class.java)
}
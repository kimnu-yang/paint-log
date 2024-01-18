package com.example.myapplication.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object KakaoClient {
    var api: KakaoService = Retrofit.Builder()
        .baseUrl("http://222.235.2.221:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(KakaoService::class.java)
}
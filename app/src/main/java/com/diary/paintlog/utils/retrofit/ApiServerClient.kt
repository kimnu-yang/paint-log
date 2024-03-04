package com.diary.paintlog.utils.retrofit

import com.diary.paintlog.BuildConfig
import com.diary.paintlog.utils.retrofit.util.LocalDateTimeConverter
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime


object ApiServerClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // 로깅 인터셉터 추가
        .build()

    var api: ApiServerService = Retrofit.Builder()
        .baseUrl(BuildConfig.API_SERVER_ADDRESS)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter())
                    .create()
            )
        )
        .client(client)
        .build()
        .create(ApiServerService::class.java)
}
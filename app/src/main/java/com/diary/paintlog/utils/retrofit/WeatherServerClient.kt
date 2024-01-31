package com.diary.paintlog.utils.retrofit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherServerClient {
    private val gson : Gson = GsonBuilder()
        .setLenient()
        .create()

    var api: WeatherServerService = Retrofit.Builder()
        .baseUrl("https://apis.data.go.kr/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(WeatherServerService::class.java)
}
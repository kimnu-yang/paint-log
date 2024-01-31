package com.diary.paintlog.utils.retrofit

import com.diary.paintlog.BuildConfig
import com.diary.paintlog.utils.retrofit.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServerService {
    @GET("1360000/VilageFcstInfoService_2.0/getVilageFcst")
    fun getWeather(
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: String,
        @Query("ny") ny: String,
        @Query("serviceKey") serviceKey: String = BuildConfig.WEATHER_KEY,
        @Query("dataType") dataType: String = "JSON",
        @Query("numOfRows") numOfRows: String = "290"
    ): Call<WeatherResponse>
}
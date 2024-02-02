package com.diary.paintlog.utils.retrofit.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("response") val result: Response
) {
    data class Response(
        @SerializedName("header") val header: WeatherHeader,
        @SerializedName("body") val body: Body
    )

    data class WeatherHeader(
        @SerializedName("resultCode") val resultCode: String,
        @SerializedName("resultMsg") val resultMsg: String,
    )
    data class Body(
        @SerializedName("dataType") val dataType: String,
        @SerializedName("items") val items: Items,
        @SerializedName("pageNo") val pageNo: String,
        @SerializedName("numOfRows") val numOfRows: String,
        @SerializedName("totalCount") val totalCount: String
    )

    data class Items(
        @SerializedName("item") val item: List<Item>
    )

    data class Item(
        @SerializedName("baseDate") val baseDate: String,
        @SerializedName("baseTime") val baseTime: String,
        @SerializedName("category") val category: String,
        @SerializedName("fcstDate") val fcstDate: String,
        @SerializedName("fcstTime") val fcstTime: String,
        @SerializedName("fcstValue") val fcstValue: String,
        @SerializedName("nx") val nx: String,
        @SerializedName("ny") val ny: String,
    )
}

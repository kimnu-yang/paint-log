package com.diary.paintlog.utils.retrofit.model

import com.google.gson.annotations.SerializedName

data class ApiRegisterResponse(
    @SerializedName("result") val result: ApiResult,
    @SerializedName("body") val data: Body
) {
    data class Body(
        @SerializedName("access_token") val accessToken: String,
        @SerializedName("access_token_expired_at") val accessTokenExpiredAt: String,
        @SerializedName("refresh_token") val refreshToken: String,
        @SerializedName("refresh_token_expired_at") val refreshTokenExpiredAt: String
    )
}

package com.diary.paintlog.utils.retrofit.model

import com.google.gson.annotations.SerializedName

data class ApiLoginResponse(
    @SerializedName("result") val result: ApiResult,
    @SerializedName("body") val data: Body
) {
    data class Body(
        @SerializedName("id") val id: Long,
        @SerializedName("status") val status: UserStatus,
        @SerializedName("kakao_user_id") val kakaoUserId: Long,
        @SerializedName("google_user_id") val googleUserId: Long,
        @SerializedName("registered_at") val registeredAt: String,
        @SerializedName("unregistered_at") val unregisteredAt: String
    )
}

package com.diary.paintlog.model

data class ApiToken(
    var accessToken: String = "",
    var accessTokenExpireAt: String = "",
    var refreshToken: String = "",
    var refreshTokenExpireAt: String = ""
)

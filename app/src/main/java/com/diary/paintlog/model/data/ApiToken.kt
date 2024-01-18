package com.diary.paintlog.model.data

data class ApiToken(
    val accessToken: String,
    val accessTokenExpireAt: String,
    val refreshToken: String,
    val refreshTokenExpireAt: String
)

package com.diary.paintlog.utils.retrofit.model

enum class UserStatus {
    REGISTERED,
    UNREGISTERED
}

fun UserStatus.description(): String {
    return when (this) {
        UserStatus.REGISTERED -> "등록"
        UserStatus.UNREGISTERED -> "해지"
    }
}
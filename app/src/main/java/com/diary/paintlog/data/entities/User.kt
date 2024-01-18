package com.diary.paintlog.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "kakao_user_id") val kakaoUserId: Long,
    @ColumnInfo(name = "google_user_id") val googleUserId: Long,
    @ColumnInfo(name = "registered_at") val registeredAt: LocalDateTime,
    @ColumnInfo(name = "unregistered_at") val unregisteredAt: LocalDateTime,
    @ColumnInfo(name = "last_login_at") val lastLoginAt: LocalDateTime
)
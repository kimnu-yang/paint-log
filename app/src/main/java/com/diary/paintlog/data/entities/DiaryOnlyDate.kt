package com.diary.paintlog.data.entities

data class DiaryOnlyDate(
    val id: Long,
    val registeredAt: String,
    val updatedAt: String?,
    val deletedAt: String?
)
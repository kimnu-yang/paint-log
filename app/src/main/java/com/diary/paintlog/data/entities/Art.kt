package com.diary.paintlog.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "art")
data class Art (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "artist") val artist: String,
    @ColumnInfo(name = "resource_id") val resourceId: String,
    @ColumnInfo(name = "rgb") val rgb: String,
    @ColumnInfo(name = "registered_at") val registeredAt: LocalDateTime? = LocalDateTime.now()
)
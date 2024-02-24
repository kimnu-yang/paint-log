package com.diary.paintlog.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "my_art")
data class MyArt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "art_id") val artId: Long,
    @ColumnInfo(name = "base_date") val baseDate: LocalDateTime,
    @ColumnInfo(name = "registered_at") val registeredAt: LocalDateTime = LocalDateTime.now()
)

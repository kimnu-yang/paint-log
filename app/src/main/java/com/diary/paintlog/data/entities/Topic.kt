package com.diary.paintlog.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "topic")
data class Topic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "topic") var topic: String,
    @ColumnInfo(name = "registered_at") var registeredAt: LocalDateTime? = LocalDateTime.now()
)

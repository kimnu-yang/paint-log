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
    @ColumnInfo(name = "red_ratio") val redRatio: Double = 0.0,
    @ColumnInfo(name = "orange_ratio") val orangeRatio: Double = 0.0,
    @ColumnInfo(name = "yellow_ratio") val yellowRatio: Double = 0.0,
    @ColumnInfo(name = "green_ratio") val greenRatio: Double = 0.0,
    @ColumnInfo(name = "blue_ratio") val blueRatio: Double = 0.0,
    @ColumnInfo(name = "navy_ratio") val navyRatio: Double = 0.0,
    @ColumnInfo(name = "violet_ratio") val violetRatio: Double = 0.0,
    @ColumnInfo(name = "registered_at") val registeredAt: LocalDateTime = LocalDateTime.now()
)
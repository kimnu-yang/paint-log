package com.diary.paintlog.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.diary.paintlog.data.entities.enums.TempStatus
import com.diary.paintlog.data.entities.enums.Weather
import java.time.LocalDateTime

@Entity(tableName = "diary")
data class Diary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "is_temp") var isTemp: TempStatus = TempStatus.Y,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "content") var content: String,
    @ColumnInfo(name = "weather") var weather: Weather? = null,
    @ColumnInfo(name = "temp_now") var tempNow: String? = null,
    @ColumnInfo(name = "temp_min") var tempMin: String? = null,
    @ColumnInfo(name = "temp_max") var tempMax: String? = null,
    @ColumnInfo(name = "registered_at") val registeredAt: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "updated_at") var updatedAt: LocalDateTime? = null,
    @ColumnInfo(name = "deleted_at") var deletedAt: LocalDateTime? = null
)
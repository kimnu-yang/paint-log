package com.diary.paintlog.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.diary.paintlog.data.entities.enums.Color

@Entity(tableName = "diary_color")
data class DiaryColor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "diary_id") var diaryId: Long,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "color") val color: Color,
    @ColumnInfo(name = "ratio") val ratio: Int
)

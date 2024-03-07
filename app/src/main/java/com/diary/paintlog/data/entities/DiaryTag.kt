package com.diary.paintlog.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_tag")
data class DiaryTag(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "diary_id") var diaryId: Long,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "tag") val tag: String
)
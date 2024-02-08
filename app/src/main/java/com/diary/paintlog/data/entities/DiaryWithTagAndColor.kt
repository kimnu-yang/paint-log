package com.diary.paintlog.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class DiaryWithTagAndColor (
    @Embedded val diary: Diary,
    @Relation(
        parentColumn = "id",
        entityColumn = "diary_id"
    )
    val tags: List<DiaryTag>,
    @Relation(
        parentColumn = "id",
        entityColumn = "diary_id"
    )
    val colors: List<DiaryColor>
)
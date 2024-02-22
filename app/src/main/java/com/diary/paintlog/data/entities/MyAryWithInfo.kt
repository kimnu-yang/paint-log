package com.diary.paintlog.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class MyAryWithInfo(
    @Embedded val myArt: MyArt,
    @Relation(
        parentColumn = "art_id",
        entityColumn = "id"
    )
    val art: List<Art>
)

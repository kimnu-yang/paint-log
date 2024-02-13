package com.diary.paintlog.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.diary.paintlog.data.entities.DiaryColor

@Dao
interface DiaryColorDao {
    @Query("SELECT * FROM diary_color WHERE diary_id = :diaryId AND position = :position")
    fun getAllDiaryColorByDiaryIdAndPosition(diaryId: Long, position: Int): DiaryColor?

    @Insert
    fun insertDiaryColor(diaryColor: DiaryColor)

    @Update
    fun updateDiaryColor(diaryColor: DiaryColor)

    @Query("DELETE FROM diary_color WHERE diary_id = :diaryId AND position = :position")
    fun deleteDiaryColorByDiaryIdAndPosition(diaryId: Long, position: Int)

    @RawQuery
    fun executeRawQuery(query: SupportSQLiteQuery): List<Any>
}
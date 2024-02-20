package com.diary.paintlog.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.diary.paintlog.data.entities.DiaryColor
import com.diary.paintlog.data.entities.DiaryColorCount

@Dao
interface DiaryColorDao {
    @Query("SELECT * FROM diary_color WHERE diary_id = :diaryId AND position = :position")
    fun getAllDiaryColorByDiaryIdAndPosition(diaryId: Long, position: Int): DiaryColor?

    @Query("SELECT color, count(1) AS count FROM diary_color WHERE diary_id IN (SELECT id FROM diary WHERE registered_at LIKE :yearMonth) GROUP BY color")
    fun getColorMonthCount(yearMonth: String): List<DiaryColorCount>

    @Insert
    fun insertDiaryColor(diaryColor: DiaryColor)

    @Update
    fun updateDiaryColor(diaryColor: DiaryColor)

    @Query("DELETE FROM diary_color WHERE diary_id = :diaryId")
    fun deleteDiaryColorByDiaryId(diaryId: Long)

    @Query("DELETE FROM diary_color WHERE diary_id = :diaryId AND position = :position")
    fun deleteDiaryColorByDiaryIdAndPosition(diaryId: Long, position: Int)

    @RawQuery
    fun executeRawQuery(query: SupportSQLiteQuery): List<Any>
}
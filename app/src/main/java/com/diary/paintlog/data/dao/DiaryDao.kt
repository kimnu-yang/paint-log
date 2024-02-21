package com.diary.paintlog.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.diary.paintlog.data.entities.Diary
import com.diary.paintlog.data.entities.DiaryWeatherCount
import com.diary.paintlog.data.entities.DiaryWithTagAndColor

@Dao
interface DiaryDao {
    @Transaction
    @Query("SELECT * FROM diary WHERE is_temp = 'N'")
    fun getAllDiaryWithTagAndColor(): List<DiaryWithTagAndColor>

    @Transaction
    @Query("SELECT * FROM diary WHERE is_temp = 'N' AND registered_at BETWEEN :startDate AND :endDate ORDER BY registered_at")
    fun getWeekDiaryByStartEndDate(startDate: String, endDate: String): List<DiaryWithTagAndColor>

    @Transaction
    @Query("SELECT * FROM diary WHERE is_temp = 'N' AND registered_at LIKE :date")
    fun getMonthDiaryWithTagAndColor(date: String): List<DiaryWithTagAndColor>

    @Query("SELECT weather, count(1) as count FROM diary WHERE is_temp = 'N' AND registered_at LIKE :yearMonth GROUP BY weather")
    fun getWeatherMonthCount(yearMonth: String): List<DiaryWeatherCount>

    @Query("SELECT * FROM diary WHERE id = :diaryId")
    fun getDiaryWithTagAndColorById(diaryId: Long): DiaryWithTagAndColor?

    @Transaction
    @Query("SELECT * FROM diary WHERE id IN (SELECT diary_id FROM diary_tag WHERE tag = :tag) ORDER BY id DESC")
    fun getAllDiaryByTagWithTagAndColor(tag: String): List<DiaryWithTagAndColor>

    @Query("SELECT * FROM diary WHERE date(registered_at) = :date AND is_temp = :temp ORDER BY id DESC LIMIT 1")
    fun getDiaryWithTagAndColorByDate(date: String, temp: String): DiaryWithTagAndColor?

    @Insert
    fun insertDiary(diary: Diary): Long

    @Update
    fun updateDiary(diary: Diary)

    @Query("DELETE FROM diary WHERE id = :diaryId")
    fun deleteDiary(diaryId: Long)

    @RawQuery
    fun executeRawQuery(query: SupportSQLiteQuery): List<Any>
}
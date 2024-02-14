package com.diary.paintlog.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.diary.paintlog.data.entities.DiaryTag
import com.diary.paintlog.data.entities.DiaryTagCount

@Dao
interface DiaryTagDao {

    @Query("SELECT tag, COUNT(1) AS count FROM diary_tag GROUP BY tag ORDER BY tag ASC")
    fun getAllTag(): List<DiaryTagCount>

    @Query("SELECT * FROM diary_tag WHERE diary_id = :diaryId AND position = :position")
    fun getAllDiaryTagByDiaryIdAndPosition(diaryId: Long, position: Int): DiaryTag?

    @Insert
    fun insertDiaryTag(diaryTag: DiaryTag)

    @Update
    fun updateDiaryTag(diaryTag: DiaryTag)

    @Query("DELETE FROM diary_tag WHERE diary_id = :diaryId AND position = :position")
    fun deleteDiaryTagByDiaryIdAndPosition(diaryId: Long, position: Int)

    @RawQuery
    fun executeRawQuery(query: SupportSQLiteQuery): List<Any>
}
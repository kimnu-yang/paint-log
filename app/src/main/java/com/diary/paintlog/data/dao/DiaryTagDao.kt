package com.diary.paintlog.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.diary.paintlog.data.entities.DiaryTag

@Dao
interface DiaryTagDao {
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
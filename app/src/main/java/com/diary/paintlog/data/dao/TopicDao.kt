package com.diary.paintlog.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.diary.paintlog.data.entities.Topic

@Dao
interface TopicDao {

    @Query("SELECT * FROM topic ORDER BY RANDOM() LIMIT 1")
    fun getRandomTopic(): Topic?
    @RawQuery
    fun executeRawQuery(query: SupportSQLiteQuery): List<Any>
}
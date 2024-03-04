package com.diary.paintlog.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.diary.paintlog.data.entities.Art

@Dao
interface ArtDao {

    @Query("SELECT * FROM art")
    fun getAllArt(): List<Art>
    @RawQuery
    fun executeRawQuery(query: SupportSQLiteQuery): List<Any>
}
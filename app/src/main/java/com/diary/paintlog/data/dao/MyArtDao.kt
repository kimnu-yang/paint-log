package com.diary.paintlog.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.diary.paintlog.data.entities.MyArt
import com.diary.paintlog.data.entities.MyArtWithInfo

@Dao
interface MyArtDao {

    @Transaction
    @Query("SELECT * FROM my_art")
    fun getAllMyArt(): List<MyArtWithInfo>

    @Transaction
    @Query("SELECT * FROM my_art WHERE date(base_date) BETWEEN :startDate AND :endDate ORDER BY ID DESC LIMIT 1")
    fun getMyArtByBaseDate(startDate: String, endDate: String): MyArtWithInfo?

    @Insert
    fun insertMyArt(myArt: MyArt)
}
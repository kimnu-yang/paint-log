package com.diary.paintlog.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.diary.paintlog.data.entities.MyArt
import com.diary.paintlog.data.entities.MyAryWithInfo
import java.time.LocalDateTime

@Dao
interface MyArtDao {

    @Transaction
    @Query("SELECT * FROM my_art")
    fun getAllMyArt(): List<MyAryWithInfo>

    @Transaction
    @Query("SELECT * FROM my_art WHERE date(base_date) BETWEEN :startDate AND :endDate ORDER BY ID DESC LIMIT 1")
    fun getMyArtByBaseDate(startDate: String, endDate: String): MyAryWithInfo?

    @Query("SELECT * FROM my_art")
    fun getMyArt(): List<MyArt>

    @Query("SELECT * FROM my_art WHERE registered_at >= :registeredAt")
    fun getMyArtByRegisteredDate(registeredAt: LocalDateTime): List<MyArt>

    @Query("SELECT id FROM my_art WHERE base_date LIKE :date ORDER BY id DESC LIMIT 1")
    fun getMyArtIdByBaseDate(date: String): Long?

    @Insert
    fun insertMyArt(myArt: MyArt)

    @Update
    fun updateMyArt(myArt: MyArt)
}
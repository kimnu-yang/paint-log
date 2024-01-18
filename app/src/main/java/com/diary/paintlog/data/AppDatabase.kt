package com.diary.paintlog.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diary.paintlog.data.converters.LocalDateTimeConverter
import com.diary.paintlog.data.dao.UserDao
import com.diary.paintlog.data.entities.User

@Database(entities = [User::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
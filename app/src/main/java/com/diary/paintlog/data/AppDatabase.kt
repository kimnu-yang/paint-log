package com.diary.paintlog.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diary.paintlog.data.converters.LocalDateTimeConverter
import com.diary.paintlog.data.dao.DiaryColorDao
import com.diary.paintlog.data.dao.DiaryDao
import com.diary.paintlog.data.dao.DiaryTagDao
import com.diary.paintlog.data.dao.UserDao
import com.diary.paintlog.data.entities.Diary
import com.diary.paintlog.data.entities.DiaryColor
import com.diary.paintlog.data.entities.DiaryTag
import com.diary.paintlog.data.entities.User

@Database(entities = [User::class, Diary::class, DiaryTag::class, DiaryColor::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun diaryDao(): DiaryDao
    abstract fun diaryTagDao(): DiaryTagDao
    abstract fun diaryColorDao(): DiaryColorDao

}
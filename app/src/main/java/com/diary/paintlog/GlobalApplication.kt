package com.diary.paintlog

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.diary.paintlog.data.AppDatabase
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "diary")

    companion object {
        lateinit var database: AppDatabase
            private set

        private lateinit var globalApplication: GlobalApplication
        fun getInstance(): GlobalApplication = globalApplication
    }

    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        globalApplication = this

        // Room 데이터베이스 인스턴스 생성
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "diary"
        ).fallbackToDestructiveMigration().build()

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
    }
}
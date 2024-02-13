package com.diary.paintlog

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import com.diary.paintlog.data.AppDatabase
import com.kakao.sdk.common.KakaoSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GlobalApplication : Application() {
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "diary")

    companion object {
        const val CHANNEL_ID = "alarm"

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

        // 알림 채널 등록
        createNotificationChannel()

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)

        // 샘플 데이터 추가 (필요 할 때만 실행할 것)
        // exampleSQL()
    }

    private fun createNotificationChannel() {
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, getString(R.string.channel_notify_title), importance).apply {
                description = getString(R.string.channel_notify_summary)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun exampleSQL() {
        CoroutineScope(Dispatchers.Default).launch {
            val diaryDao = database.diaryDao()
            val diaryTagDao = database.diaryTagDao()
            val diaryColorDao = database.diaryColorDao()

            // ---------------------------------------------------------------------------- 일기만 작성된 예시 5개 ----------------------------------------------------------------------------------------------------
            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목11', '내용11', 'SUNNY', '20', '10', '25', '2024-01-29T11:09:11.332798')"))

            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목12', '내용12', 'CLOUDY', '20', '10', '25', '2024-01-30T11:09:11.332798')"))

            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목13', '내용13', 'LITTLE_RAIN', '20', '10', '25', '2024-01-31T11:09:11.332798')"))

            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목14', '내용14', 'RAINY', '20', '10', '25', '2024-02-01T11:09:11.332798')"))

            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목15', '내용15', 'SNOWY', '20', '10', '25', '2024-02-02T11:09:11.332798')"))

            // ---------------------------------------------------------------------------- 일기, 태그 작성된 예시 5개 ----------------------------------------------------------------------------------------------------
            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목21', '내용21', 'SUNNY', '20', '10', '25', '2024-02-05T11:09:11.332798')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 1, '태그1')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 2, '태그2')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 3, '태그3')"))

            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목22', '내용22', 'OVERCAST', '20', '10', '25', '2024-02-06T11:09:11.332798')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 1, '태그1')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 2, '태그2')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 3, '태그3')"))

            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목23', '내용23', 'LITTLE_RAIN', '20', '10', '25', '2024-02-07T11:09:11.332798')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 1, '태그1')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 2, '태그2')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 3, '태그3')"))

            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목24', '내용24', 'OVERCAST', '20', '10', '25', '2024-02-08T11:09:11.332798')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 1, '태그1')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 2, '태그2')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 3, '태그3')"))

            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목25', '내용25', 'SNOWY', '20', '10', '25', '2024-02-09T11:09:11.332798')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 1, '태그1')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 2, '태그2')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 3, '태그3')"))

            // ---------------------------------------------------------------------------- 일기, 태그, 색 전부 있는 예시 5개 ----------------------------------------------------------------------------------------------------
            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목31', '내용31', 'SUNNY', '20', '10', '25', '2024-02-12T11:09:11.332798')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 1, '태그1')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 2, '태그2')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 3, '태그3')"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 1, 'RED', 60)"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 2, 'BLUE', 30)"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 3, 'VIOLET', 50)"))

            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목32', '내용32', 'CLOUDY', '20', '10', '25', '2024-02-13T11:09:11.332798')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 1, '태그1')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 2, '태그2')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 3, '태그3')"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 1, 'RED', 60)"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 2, 'BLUE', 30)"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 3, 'VIOLET', 50)"))

            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목33', '내용33', 'CLOUDY', '20', '10', '25', '2024-02-14T11:09:11.332798')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 1, '태그1')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 2, '태그2')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 3, '태그3')"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 1, 'RED', 60)"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 2, 'BLUE', 30)"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 3, 'VIOLET', 50)"))

            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목34', '내용34', 'RAINY', '20', '10', '25', '2024-02-15T11:09:11.332798')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 1, '태그1')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 2, '태그2')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 3, '태그3')"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 1, 'RED', 60)"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 2, 'BLUE', 30)"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 3, 'VIOLET', 50)"))

            diaryDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary(is_temp, title, content, weather, temp_now, temp_min, temp_max, registered_at) values ('N', '제목35', '내용35', 'RAINY', '20', '10', '25', '2024-02-16T11:09:11.332798')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 1, '태그1')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 2, '태그2')"))
            diaryTagDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_tag(diary_id, position, tag) values ((SELECT id FROM diary order by id desc limit 1), 3, '태그3')"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 1, 'RED', 60)"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 2, 'BLUE', 30)"))
            diaryColorDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO diary_color(diary_id, position, color, ratio) values ((SELECT id FROM diary order by id desc limit 1), 3, 'VIOLET', 50)"))
        }
    }
}
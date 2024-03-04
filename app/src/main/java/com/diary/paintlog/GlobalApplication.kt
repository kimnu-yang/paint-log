package com.diary.paintlog

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import com.diary.paintlog.data.AppDatabase
import com.diary.paintlog.utils.SyncDataManager
import com.kakao.sdk.common.KakaoSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

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
        ).fallbackToDestructiveMigration().setQueryCallback({ sqlQuery, bindArgs ->
            Log.d("SQL", "${sqlQuery}, args=${bindArgs}")
        }, Executors.newSingleThreadExecutor()).build()

        // 알림 채널 등록
        createNotificationChannel()

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)

        // 동기화 실행
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                SyncDataManager().syncData(applicationContext)
                delay(30000) // 10초마다 작업 실행
            }
        }

        // 샘플 데이터 추가 (필요 할 때만 실행할 것)
//        exampleSQL()

        // 주제 샘플 데이터 (필요 할 때만 실행할 것)
//        exampleTopic()

        // 그림 데이터 추가
//        imageDataSQL()
    }

    private fun createNotificationChannel() {
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_notify_title),
                importance
            ).apply {
                description = getString(R.string.channel_notify_summary)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun exampleTopic() {
        CoroutineScope(Dispatchers.Default).launch {
            val topicDao = database.topicDao()

            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('내가 가장 좋아하는 음악은?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('내가 가장 좋아하는 음식은?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('내가 열정을 가지는 분야는?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('인생에서 가장 중요한것은?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('나 스스로를 묘사한다면?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('나는 지금 내가 꿈꾸던대로 살고 있나요?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('3년전 나에게 해주고 싶은 충고는?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('오늘 무엇때문에 바쁜가요? 이것이 1년 후, 3년 후, 5년 후에도 문제가 될까요?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('앞으로 인생이 자유라면, 무엇을 하고 싶은가요?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('지금 인생의 가장 우선순위는?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('10억을 가지고 있다면, 무엇을 할것인지?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('인생에서 가장 두려운 것은?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('없애고 싶은 나의 나쁜 습관은?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('더 키우고 싶은 나의 좋은 습관은?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('내가 가장 많은 시간을 함께 보내는 5명')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('나에게 가장 영감을 주는 사람은?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('세상에서 나에게 가장 중요한 사람(들)은?')"))
            topicDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO topic(topic) VALUES ('당신 인생의 멘토는 누구인지?')"))
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

    private fun imageDataSQL() {
        CoroutineScope(Dispatchers.Default).launch {
            val artDao = database.artDao()

            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0101','굴이 있는 정물화','귀스타브 카유보트','ffbfb3b3')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0102','발코니','귀스타브 카유보트','ff8d7364')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0103','보트 파티','귀스타브 카유보트','ff797869')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0104','예레스, 비','귀스타브 카유보트','ff776f57')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0105','오렌지 나무','귀스타브 카유보트','ff747162')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0106','유럽의 다리','귀스타브 카유보트','ff727a77')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0107','정원사','귀스타브 카유보트','ffaea692')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0108','창문 앞의 남자','귀스타브 카유보트','ff444640')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0109','트루빌의 빌라','귀스타브 카유보트','ffb4b2a1')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0110','파리의 거리, 비오는 날','귀스타브 카유보트','ff888980')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0201','안녕하십니까, 쿠르베 씨','귀스타브 쿠르베','ff959578')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0202','폭풍이 지나간 후의 에트르타 절벽','귀스타브 쿠르베','ff869193')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0203','프루동과 그의 아이들','귀스타브 쿠르베','ff555640')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0301','공원의 가로수길','빈센트 반 고흐','ff675c42')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0302','꽃 피는 아몬드 나무','빈센트 반 고흐','ff5f8d87')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0303','론강의 별이 빛나는 밤','빈센트 반 고흐','ff313b38')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0304','밀짚 모자를 쓴 자화상','빈센트 반 고흐','ffa49661')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0305','밤의 카페 테라스','빈센트 반 고흐','ff4e5143')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0306','별이 빛나는 밤','빈센트 반 고흐','ff495b6f')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0307','삼나무가 있는 밀 밭','빈센트 반 고흐','ff909a82')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0308','아를의 침실','빈센트 반 고흐','ff817f64')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0309','자화상','빈센트 반 고흐','ff869c8b')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0310','해바라기','빈센트 반 고흐','ffb7a845')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0311','회색 모자를 쓴 자화상','빈센트 반 고흐','ff5b6861')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0401','숲 속 작은길','아르망 기요맹','ff7d6f46')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0402','이브리의 석양','아르망 기요맹','ffb29156')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0403','일드 프랑스의 경치','아르망 기요맹','ff7c8e80')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0404','일드 프랑스의 풍경','아르망 기요맹','ff8e8c61')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0405','주앵빌 강에 있는 다리','아르망 기요맹','ff77724f')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0406','퓌드폼 풍경','아르망 기요맹','ff7c8989')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0501','루브시엔느의 눈','알프레드 시슬레','ffbbb8be')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0502','모레쉬르루앙의 포플러 나무 길','알프레드 시슬레','ff636c70')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0503','모레의 다리','알프레드 시슬레','ff748b97')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0504','빌뇌브라가렌의 다리','알프레드 시슬레','ff8d9ca1')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0505','새벽의 세느강','알프레드 시슬레','ff7b8883')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0506','생 마메스의 전망','알프레드 시슬레','ff939290')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0507','햄프턴 코트의 템스강','알프레드 시슬레','ff95936a')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0601','개구리 연못','오귀스트 르누아르','ff6c7265')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0602','그네','오귀스트 르누아르','ff606562')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0603','노젓는 사람들의 오찬','오귀스트 르누아르','ff49493d')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0604','물랭 드 라 갈레트의 무도회','오귀스트 르누아르','ff565955')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0701','그라비린 수로','조르주 쇠라','ffc6cfc7')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0702','그랑드자트섬의 일요일 오후','조르주 쇠라','ff686f59')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0703','아스니에르에서 물놀이하는 사람들','조르주 쇠라','ff909896')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0801','루브시엔느에서 베르사유로 가는 길','카미유 피사로','ff8e8c88')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0802','몽마르트 대로, 밤','카미유 피사로','ff403e47')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0803','몽마르트 대로, 오후','카미유 피사로','ff7f7459')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0804','베르사유 도로','카미유 피사로','ff818b84')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0805','에라니 마을의 전경','카미유 피사로','ff9a9574')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0806','에라의 농민주택','카미유 피사로','ff899f73')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0807','입구와 범선','카미유 피사로','ffaaab9f')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0808','잘라이스 언덕','카미유 피사로','ff6f775f')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('0901','안개 바다 위의 방랑자','카스파르 다비트 프리드리히','ff8d919d')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('1001','개구리 연못','클로드 모네','ff616e56')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('1002','까치','클로드 모네','ffa7adac')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('1003','생 트아트레스 해변','클로드 모네','ff879693')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('1004','수련','클로드 모네','ff4f6d4d')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('1005','양귀비','클로드 모네','ffa5a690')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('1006','지베르니 화가의 정원','클로드 모네','ff797361')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('1007','파라솔을 든 여인','클로드 모네','ff6d7467')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('1008','푸르빌의 절벽 산책로','클로드 모네','ff869898')"))
            artDao.executeRawQuery(SimpleSQLiteQuery("INSERT INTO art (resource_id, title, artist, rgb) values ('1009','풀밭위의 점심식사','클로드 모네','ff6d6333')"))
        }
    }
}
package com.diary.paintlog.utils

import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.R
import com.diary.paintlog.data.entities.DiaryOnlyDate
import com.diary.paintlog.data.entities.DiaryWithTagAndColor
import com.diary.paintlog.data.entities.MyArt
import com.diary.paintlog.data.entities.enums.TempStatus
import com.diary.paintlog.databinding.FragmentSettingsBinding
import com.diary.paintlog.model.repository.SettingsRepository
import com.diary.paintlog.utils.retrofit.ApiServerClient
import com.diary.paintlog.utils.retrofit.model.CheckSyncDataRequest
import com.diary.paintlog.utils.retrofit.model.SyncResponse
import com.diary.paintlog.view.dialog.LoadingDialog
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SyncDataManager {

    private val TAG = this.javaClass.simpleName

    private val diaryDao = GlobalApplication.database.diaryDao()
    private val diaryTagDao = GlobalApplication.database.diaryTagDao()
    private val diaryColorDao = GlobalApplication.database.diaryColorDao()

    private val myArtDao = GlobalApplication.database.myArtDao()

    private val settingsRepo = SettingsRepository()

    private var loading: LoadingDialog? = null

    private fun getDiarySyncList(lastSyncTime: String): List<DiaryOnlyDate> {
        return if (lastSyncTime == "") {
            diaryDao.getAllDiaryRegisteredAt()
        } else {
            diaryDao.getAllDiaryRegisteredAt(LocalDateTime.parse(lastSyncTime))
        }
    }

    private fun getMyArtSyncList(lastSyncTime: String): List<MyArt> {
        return if (lastSyncTime == "") {
            myArtDao.getMyArt()
        } else {
            myArtDao.getMyArtByRegisteredDate(LocalDateTime.parse(lastSyncTime))
        }
    }

    private fun getUploadList(diaryIds: List<Long>): List<DiaryWithTagAndColor> {
        return diaryDao.getAllDiaryWithTagAndColor(diaryIds)
    }

    private fun saveDiary(diaryList: List<DiaryWithTagAndColor>) {
        diaryList.forEach {
            val oldDiaryId =
                diaryDao.getDiaryId(it.diary.registeredAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "%")
            val diaryId: Long
            it.diary.isTemp = TempStatus.N

            if (oldDiaryId != null) {
                // 업데이트인 경우
                diaryId = oldDiaryId
                it.diary.id = diaryId
                diaryDao.updateDiary(it.diary)

                // 업데이트인 경우에는 태그, 컬러는 삭제 후 insert 처리
                diaryTagDao.deleteDiaryTagByDiaryId(diaryId)
                diaryColorDao.deleteDiaryColorByDiaryId(diaryId)
            } else {
                diaryId = diaryDao.insertDiary(it.diary)
            }

            it.colors.forEach { color ->
                color.diaryId = diaryId
            }
            it.tags.forEach { tags ->
                tags.diaryId = diaryId
            }

            diaryTagDao.insertDiaryTagAll(it.tags)
            diaryColorDao.insertDiaryColorAll(it.colors)
        }
    }

    private fun saveMyArt(myArtList: List<MyArt>) {
        myArtList.forEach {
            val oldMyArtId =
                myArtDao.getMyArtIdByBaseDate(
                    it.baseDate.format(
                        DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd"
                        )
                    ) + "%"
                )

            if (oldMyArtId != null) {
                it.id = oldMyArtId
                myArtDao.updateMyArt(it)
            } else {
                myArtDao.insertMyArt(it)
            }
        }
    }

    fun syncData(context: Context, binding: FragmentSettingsBinding? = null) {
        val isSettingsScreen = binding != null

        // 인터넷 연결 확인
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        if (networkCapabilities == null) {
            // 네트워크에 연결되어 있지 않음
            if (isSettingsScreen) {
                Toast.makeText(context, "네트워크 연결 후 시도 바랍니다.", Toast.LENGTH_SHORT).show()
            }
            return
        }

        // 카카오 토큰이 없는 경우는 return
        if (!AuthApiClient.instance.hasToken()) {
            Log.d(TAG, "토큰 오류")
            return
        }

        if (isSettingsScreen) {
            loading = LoadingDialog(context)
            loading?.show()
        }

        // 카카오 트큰이 존재하는 경우 토큰 갱신 먼저 진행 (API 서버에서 조회할 때 필요)
        UserApiClient.instance.accessTokenInfo { _, _ -> }

        CoroutineScope(Dispatchers.IO).launch {
            // app 저장 데이터 조회
            // 설정 화면에서 누르는 경우 전체 전송
            val lastSyncTime = if (isSettingsScreen) {
                ""
            } else {
                runBlocking { settingsRepo.getSyncTime() }
            }
            val appDiaryData = getDiarySyncList(lastSyncTime)
            val myArtData = getMyArtSyncList(lastSyncTime)
            val kakaoToken =
                AuthApiClient.instance.tokenManagerProvider.manager.getToken()?.accessToken ?: ""
            val checkSyncDataRequest = CheckSyncDataRequest(lastSyncTime, appDiaryData, myArtData)

            ApiServerClient.api.syncDataCheck(kakaoToken, checkSyncDataRequest)
                .enqueue(object : Callback<SyncResponse> {
                    override fun onResponse(
                        call: Call<SyncResponse>, response: Response<SyncResponse>
                    ) {
                        if (response.code() == 500) {
                            if (isSettingsScreen) {
                                Toast.makeText(binding?.root?.context, "서버 오류", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        var isEndUpload = false
                        var isEndDownload = false

                        // 업로드할 데이터
                        CoroutineScope(Dispatchers.IO).launch {
                            val uploadList = getUploadList(
                                response.body()?.data?.uploadList ?: listOf()
                            )

                            if (uploadList.isNotEmpty()) {
                                ApiServerClient.api.uploadDiary(kakaoToken, uploadList)
                                    .enqueue(object : Callback<Any> {
                                        override fun onResponse(
                                            call: Call<Any>, response: Response<Any>
                                        ) {
                                            isEndUpload = true
                                            if (isEndDownload) {
                                                loading?.dismiss()
                                                setSettingsSyncTime(binding)
                                            }
                                        }

                                        override fun onFailure(call: Call<Any>, t: Throwable) {
                                            if (isSettingsScreen) {
                                                Toast.makeText(
                                                    context, t.localizedMessage, Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            isEndUpload = true
                                            if (isEndDownload) {
                                                loading?.dismiss()
                                            }
                                        }

                                    })
                            } else {
                                isEndUpload = true
                                if (isEndDownload) {
                                    loading?.dismiss()
                                    setSettingsSyncTime(binding)
                                }
                            }
                        }

                        // 다운로드할 데이터
                        val downloadList = response.body()?.data?.downloadList ?: listOf()
                        val myArtDownloadList = response.body()?.data?.myArtDownloadList ?: listOf()
                        CoroutineScope(Dispatchers.IO).launch {
                            saveDiary(downloadList)
                            saveMyArt(myArtDownloadList)
                            isEndDownload = true
                            if (isEndUpload) {
                                loading?.dismiss()
                                setSettingsSyncTime(binding)
                            }
                        }
                    }

                    override fun onFailure(call: Call<SyncResponse>, t: Throwable) {
                        loading?.dismiss()

                        if (isSettingsScreen) {
                            Toast.makeText(context, t.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }

                })
        }
    }

    private fun setSettingsSyncTime(binding: FragmentSettingsBinding?) {
        val now = LocalDateTime.now()
        CoroutineScope(Dispatchers.IO).launch {
            settingsRepo.saveSyncTime(now)
        }

        if (binding != null) {
            // 한국 시간 포맷 지정
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
            Handler(Looper.getMainLooper()).post {
                binding.settingSyncSummary.text =
                    binding.root.context.getString(
                        R.string.setting_sync_summary,
                        now.format(formatter)
                    )
            }
        }
    }

}
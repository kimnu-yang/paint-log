package com.diary.paintlog.view.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.diary.paintlog.R
import com.diary.paintlog.databinding.FragmentSettingsBinding
import com.diary.paintlog.model.repository.SettingsRepository
import com.diary.paintlog.utils.Kakao
import com.diary.paintlog.utils.retrofit.ApiServerClient
import com.diary.paintlog.utils.retrofit.model.ApiLoginResponse
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

class SettingsFragment : Fragment() {

    private val TAG = "SettingsFragment"

    private var _binding: FragmentSettingsBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 접근용 getter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 초기화
        _binding = FragmentSettingsBinding.inflate(inflater, container, false) // 바인딩 객체 초기화

        val settingsRepo = SettingsRepository()
        /////

        // 알람 설정 세팅

        // 설정한 알람 시간 표시
        val time: SettingsRepository.Time = runBlocking { settingsRepo.getTime() }

        if (time.hour != -1) {
            binding.settingAlarmSummary.text = setTimeMsg(time.hour, time.minute)
        }

        binding.settingAlarmView.setOnClickListener {
            val nowTime = runBlocking { settingsRepo.getTime() }

            TimePickerDialog(
                context,
                { _, selectedHour, selectedMinute ->
                    runBlocking { settingsRepo.saveTime(selectedHour, selectedMinute) }
                    reloadFragment()
                },
                nowTime.hour,
                nowTime.minute,
                true
            ).show()
        }
        /////

        // 알람 스위치 세팅
        val checked = runBlocking { settingsRepo.getChecked() }

        binding.settingAlarmSwitch.isChecked = checked

        binding.settingAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.IO).launch { settingsRepo.saveChecked(isChecked) }
        }
        /////

        // 동기화 세팅
        val syncTime = runBlocking { settingsRepo.getSyncTime() }
        // 한국 시간 포맷 지정
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

        val syncText = if (syncTime != "") {
            val lastSyncTime: LocalDateTime = LocalDateTime.parse(syncTime)
            getString(R.string.setting_sync_summary, lastSyncTime.format(formatter))
        } else {
            getString(R.string.setting_sync_summary, getString(R.string.setting_sync_summary_none))
        }

        binding.settingSyncSummary.text = syncText
        binding.settingSyncButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                settingsRepo.saveSyncTime(LocalDateTime.now())
                reloadFragment()
            }
        }
        /////

        // 카카오 로그인, 로그아웃 세팅
        if (AuthApiClient.instance.hasToken()) {
            // 토큰이 있는 경우 로그아웃 버튼 표시
            binding.settingKakaoLoginButton.visibility = View.GONE
            binding.settingUnregistText.visibility = View.VISIBLE
            binding.settingKakaoLogoutButton.visibility = View.VISIBLE
        }

        binding.settingKakaoLoginButton.setOnClickListener {
            Kakao.openKakaoLogin(requireContext()) {
                reloadFragment()
            }
        }
        binding.settingKakaoLogoutButton.setOnClickListener {
            Kakao.kakaoLogout(requireContext()) {
                reloadFragment()
            }
        }
        /////

        // 탈퇴하기
        binding.settingUnregistText.setOnClickListener {
            val kakaoToken =
                AuthApiClient.instance.tokenManagerProvider.manager.getToken()?.accessToken ?: ""
            if (kakaoToken == "") {
                Toast.makeText(context, "재접속 후 다시 시도 바랍니다.", Toast.LENGTH_SHORT).show()
            } else {

                ApiServerClient.api.unregistKakaoUser(kakaoToken).enqueue(object :
                    Callback<ApiLoginResponse> {
                    override fun onResponse(
                        call: Call<ApiLoginResponse>,
                        response: Response<ApiLoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.i(Kakao.TAG, "${response.body()}")

                            // 카카오 연결 끊기
                            UserApiClient.instance.unlink { error ->
                                if (error != null) {
                                    Log.e(TAG, "연결 끊기 실패", error)
                                } else {
                                    Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                                    reloadFragment()
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                getString(R.string.setting_unregist_error, "-1"),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i(Kakao.TAG, response.toString())
                        }
                    }

                    override fun onFailure(call: Call<ApiLoginResponse>, t: Throwable) {
                        Toast.makeText(
                            context,
                            getString(R.string.setting_unregist_error, "0"),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i(Kakao.TAG, t.localizedMessage?.toString() ?: "ERROR")
                    }
                })
            }
        }
        /////

        return binding.root // 뷰 반환
    }

    /**
     * 시간 텍스트 설정 함수
     */
    private fun setTimeMsg(hour: Int, minute: Int): String {
        return hour.toString() + resources.getString(R.string.setting_hour) + " " + minute.toString() + resources.getString(
            R.string.setting_minute
        )
    }

    /**
     * 설정 화면 reload
     */
    private fun reloadFragment() {
        findNavController().popBackStack(R.id.action_global_settingsFragment, true)
        findNavController().navigate(R.id.action_global_settingsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 바인딩 객체 해제
    }
}
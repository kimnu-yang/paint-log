package com.diary.paintlog.view.fragments

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
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
import com.diary.paintlog.utils.NotifyManager
import com.diary.paintlog.utils.SyncDataManager
import com.diary.paintlog.utils.retrofit.ApiServerClient
import com.diary.paintlog.utils.retrofit.model.ApiLoginResponse
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

class SettingsFragment : Fragment() {

    private val tag = this.javaClass.simpleName

    private var _binding: FragmentSettingsBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 접근용 getter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // 초기화
        _binding = FragmentSettingsBinding.inflate(inflater, container, false) // 바인딩 객체 초기화

        val settingsRepo = SettingsRepository()

        val alarmChecked = runBlocking { settingsRepo.getChecked() }
        var storeTime = runBlocking { settingsRepo.getTime() }
        /////

        // 알람 설정 세팅

        if (storeTime.hour != -1 && alarmChecked) {
            binding.settingAlarmSummary.text = setTimeMsg(storeTime)
        }

        val timePickerDialog = TimePickerDialog(
            context, { _, selectedHour, selectedMinute ->
                runBlocking { settingsRepo.saveTime(selectedHour, selectedMinute) }
                binding.settingAlarmSummary.text =
                    setTimeMsg(SettingsRepository.Time(selectedHour, selectedMinute))

                if (binding.settingAlarmSwitch.isChecked) {
                    NotifyManager.setAlarm(requireContext())
                } else {
                    binding.settingAlarmSwitch.isChecked = true
                }
            }, storeTime.hour, storeTime.minute, true
        )

        binding.settingAlarmView.setOnClickListener {
            storeTime = runBlocking { settingsRepo.getTime() }

            timePickerDialog.show()
        }
        /////

        // 알람 스위치 세팅
        binding.settingAlarmSwitch.isChecked = alarmChecked

        binding.settingAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!NotifyManager.checkNotifyPermissionWithRequest(requireContext())) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.setting_notify_disabled),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.settingAlarmSwitch.isChecked = false
                    return@setOnCheckedChangeListener
                }

                NotifyManager.checkNotifyPermissionWithRequest(requireContext())

                storeTime = runBlocking { settingsRepo.getTime() }

                if (storeTime.hour == -1) {
                    binding.settingAlarmView.callOnClick()
                }

                binding.settingAlarmSummary.text = setTimeMsg(storeTime)
            } else {
                binding.settingAlarmSummary.text = getString(R.string.setting_alarm_summary)
            }

            runBlocking { settingsRepo.saveChecked(isChecked) }

            if (isChecked) {
                NotifyManager.setAlarm(requireContext())
            } else {
                NotifyManager.cancelAlarm(requireContext())
            }
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
            SyncDataManager().syncData(requireContext(), binding)
        }
        /////

        // 카카오 로그인, 로그아웃 세팅
        if (AuthApiClient.instance.hasToken()) {
            // 토큰이 있는 경우 로그아웃 버튼 표시
            binding.settingKakaoLoginButton.visibility = View.GONE
            binding.settingUnregistText.visibility = View.VISIBLE
            binding.settingKakaoLogoutButton.visibility = View.VISIBLE

            binding.settingSyncView.visibility = View.VISIBLE
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
        val loading = LoadingDialog(requireContext())

        binding.settingUnregistText.setOnClickListener {
            val kakaoToken =
                AuthApiClient.instance.tokenManagerProvider.manager.getToken()?.accessToken ?: ""
            if (kakaoToken == "") {
                Toast.makeText(
                    context, getString(R.string.setting_unregist_error_retry), Toast.LENGTH_SHORT
                ).show()
            } else {
                showConfirmationDialog(
                    requireContext(), getString(R.string.setting_unregist_confirm)
                ) {
                    loading.show()

                    ApiServerClient.api.unregistKakaoUser(kakaoToken)
                        .enqueue(object : Callback<ApiLoginResponse> {
                            override fun onResponse(
                                call: Call<ApiLoginResponse>, response: Response<ApiLoginResponse>
                            ) {
                                if (response.isSuccessful) {
                                    // 카카오 연결 끊기
                                    UserApiClient.instance.unlink { error ->
                                        if (error != null) {
                                            Log.e(tag, "연결 끊기 실패", error)
                                        } else {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                settingsRepo.delSyncTime()
                                            }
                                            reloadFragment()
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        getString(R.string.setting_unregist_error, "-1"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                loading.dismiss()
                            }

                            override fun onFailure(call: Call<ApiLoginResponse>, t: Throwable) {
                                Toast.makeText(
                                    context,
                                    getString(R.string.setting_unregist_error, "0"),
                                    Toast.LENGTH_SHORT
                                ).show()
                                loading.dismiss()
                            }
                        })
                }
            }
        }
        /////

        return binding.root // 뷰 반환
    }

    /**
     * 시간 텍스트 설정 함수
     */
    private fun setTimeMsg(time: SettingsRepository.Time): String {
        return if (time.hour != -1) {
            "${time.hour}${resources.getString(R.string.setting_hour)} ${time.minute}${
                resources.getString(
                    R.string.setting_minute
                )
            }"
        } else {
            getString(R.string.setting_alarm_summary)
        }
    }

    /**
     * 설정 화면 reload
     */
    private fun reloadFragment() {
        findNavController().navigate(R.id.action_global_fragment_settings)
    }

    private fun showConfirmationDialog(context: Context, message: String, onConfirmed: () -> Unit) {
        AlertDialog.Builder(context).setMessage(message).setPositiveButton("확인") { dialog, _ ->
                // "확인" 버튼을 클릭하면 onConfirmed 함수를 호출합니다.
                onConfirmed()
                dialog.dismiss()
            }.setNegativeButton("취소") { dialog, _ ->
                // "취소" 버튼을 클릭하면 대화 상자를 닫습니다.
                dialog.dismiss()
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 바인딩 객체 해제
    }
}
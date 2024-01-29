package com.diary.paintlog.view.fragments

import android.app.TimePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.diary.paintlog.R
import com.diary.paintlog.databinding.FragmentSettingsBinding
import com.diary.paintlog.model.repository.SettingsRepository
import com.diary.paintlog.utils.Kakao
import com.kakao.sdk.auth.AuthApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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

        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
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
            binding.settingQuitText.visibility = View.VISIBLE
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
        findNavController().popBackStack(R.id.action_global_settingsFragment, true);
        findNavController().navigate(R.id.action_global_settingsFragment)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 바인딩 객체 해제
    }
}
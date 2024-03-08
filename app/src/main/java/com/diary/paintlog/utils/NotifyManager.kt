package com.diary.paintlog.utils

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.R
import com.diary.paintlog.model.repository.SettingsRepository
import com.diary.paintlog.view.activities.MainActivity
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


object NotifyManager {

    private val TAG = this.javaClass.simpleName
    private const val requestCode = 0
    private const val requestId = 0

    private fun checkNotifyPermission(context: Context): Boolean {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.areNotificationsEnabled()
    }

    private fun checkChannelPermission(context: Context): Boolean {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = notificationManager.getNotificationChannel(GlobalApplication.CHANNEL_ID)

        // 권한이 있는 경우
        // 알림 권한은 있지만 카테고리 권한이 없을 때
        return channel.importance != NotificationManager.IMPORTANCE_NONE
    }

    /**
     * @return 권한 요청 여부 true: 정상 권한, false: 권한 없음
     */
    fun checkNotifyPermissionWithRequest(context: Context): Boolean {
        if (!checkNotifyPermission(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                ) {
                    // 권한 요청할 때 toast로 필요한 이유 표시 (거절한 경우에 다시 들어와도 표시)
                    Toast.makeText(
                        context,
                        context.getString(R.string.setting_notify_disabled),
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                } else {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        requestCode
                    )
                    return false
                }
            } else {
                settingDialog(context)
                return false
            }
        } else {
            if (!checkChannelPermission(context)) {
                settingDialog(context, false)
                return false
            }

            return true
        }
    }

    private fun settingDialog(context: Context, isChannel: Boolean = false) {
        // 알림 설정이 꺼져 있는 경우
        // 사용자에게 알림 권한을 요청하는 대화상자를 표시
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(R.string.setting_notify_dialog_title)
        dialog.setMessage(context.getString(R.string.setting_notify_dialog_summary))
        dialog.setPositiveButton("확인") { _, _ ->
            // 알림 설정 화면으로 이동하는 인텐트 생성
            val settingIntent = Intent()
            if (isChannel) {
                settingIntent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            } else {
                settingIntent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                settingIntent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                settingIntent.putExtra(Settings.EXTRA_CHANNEL_ID, GlobalApplication.CHANNEL_ID)
            }

            // 알림 설정 화면으로 이동
            context.startActivity(settingIntent)
        }
        dialog.setNegativeButton("취소", null)
        dialog.show()
    }

    fun notify(context: Context, title: String, content: String) {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity::class.java).apply {
            // 특정 hostFragment를 표시하기 위한 정보를 Intent에 추가
            putExtra("destination_fragment", R.id.action_fragment_main_to_fragment_diary)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, GlobalApplication.CHANNEL_ID)
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Notification Manager를 통해 Notification을 notify
        val notificationManager = NotificationManagerCompat.from(context)

        if (checkNotifyPermission(context)) {
            if (checkChannelPermission(context)) {
                notificationManager.notify(requestId, builder.build())
            } else {
                Log.e(TAG, "NO CHANNEL PERMISSION")
            }
        } else {
            Log.e(TAG, "NO NOTIFY PERMISSION")
        }
    }

    fun setAlarm(context: Context, isRepeat: Boolean = false) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getAlarm(context)

        val settingsRepository = SettingsRepository()
        val alarmTime = runBlocking { settingsRepository.getTime() }
        val alarmChecked = runBlocking { settingsRepository.getChecked() }

        // 알람 설정 옵션이 켜진 경우
        if (alarmTime.hour != -1 && alarmChecked) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, alarmTime.hour)
            calendar.set(Calendar.MINUTE, alarmTime.minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            if (isRepeat) calendar.add(Calendar.DAY_OF_MONTH, 1)

            val now = Calendar.getInstance()
            // 현재 시간과 알람 설정 시간을 비교하여 알람 설정 시간이 이미 지났는지 확인
            if (calendar.before(now)) {
                // 알람 설정 시간이 이미 지난 경우, 내일로 설정
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d(
                TAG,
                "알람 설정 On [now: ${millisToHourMinuteSecond(System.currentTimeMillis())}] [set: ${
                    millisToHourMinuteSecond(calendar.timeInMillis)
                }]"
            )
        } else {
            Log.e(TAG, "알람 설정 status off [hour: ${alarmTime.hour}, checked: ${alarmChecked}]")
        }
    }

    fun cancelAlarm(context: Context) {
        getAlarm(context).cancel()
        Log.d(TAG, "알람 삭제")
    }

    private fun getAlarm(context: Context): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.setAction(NotificationReceiver.notificationAction)
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun millisToHourMinuteSecond(sec: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = sec
        return sdf.format(calendar.time)
    }
}
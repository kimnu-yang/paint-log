package com.diary.paintlog.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.R
import com.diary.paintlog.view.activities.MainActivity

object NotifyManager {

    const val requestCode = 0
    private const val requestId = 0

    fun checkNotifyPermission(context: Context): Boolean {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.areNotificationsEnabled()
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
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = notificationManager.getNotificationChannel(GlobalApplication.CHANNEL_ID)
            // 권한이 있는 경우
            // 알림 권한은 있지만 카테고리 권한이 없을 때
            if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                settingDialog(context, false)
                return false
            }

            return true
        }
    }

    fun settingDialog(context: Context, isChannel: Boolean = false) {
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
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        val builder = NotificationCompat.Builder(context, GlobalApplication.CHANNEL_ID)
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // AlarmManager 통해 알림 보낼거라 권한이 없을 땐 notify X
                return
            }

            notify(requestId, builder.build())
        }

//        with(NotificationManagerCompat.from(context)) {
//            // notificationId is a unique int for each notification that you must define
//            if (ActivityCompat.checkSelfPermission(
//                    context
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                // 권한이 없는 경우 권한 요청
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTI_PERMISSION_CODE)
//                } else {
//                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                    val areNotificationsEnabled = notificationManager.areNotificationsEnabled()
//
//                    if (!areNotificationsEnabled) {
//                        // 알림 설정이 꺼져 있는 경우
//                        // 사용자에게 알림 권한을 요청하는 대화상자를 표시
//                        val dialog = AlertDialog.Builder(this@MainActivity)
//                        dialog.setTitle("알림 허용")
//                        dialog.setMessage("알림을 허용하시겠습니까?")
//                        dialog.setPositiveButton("확인") { _, _ ->
//                            // 알림 설정 화면으로 이동하는 인텐트 생성
//                            val settingIntent = Intent()
//                            settingIntent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
//                            settingIntent.putExtra(Settings.EXTRA_APP_PACKAGE, this@MainActivity.packageName)
//
//                            // 알림 설정 화면으로 이동
//                            this@MainActivity.startActivity(settingIntent)
//                        }
//                        dialog.setNegativeButton("취소", null)
//                        dialog.show()
//                    }
//                }
//                return
//            }
//
//            notify(0, builder.build())
//        }
    }


//    fun setAlarm(context: Context, alarmTimeMillis: Long) {
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(
//            context, requestCode, intent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            if (alarmManager.canScheduleExactAlarms()) {
//                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent)
//            } else {
//                TODO("알림 권한 요청 넣기")
//            }
//        } else {
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent)
//        }
//
//    }
//
//    fun cancelAlarm(context: Context) {
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(
//            context, 0, intent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//
//        // 알림을 취소
//        alarmManager.cancel(pendingIntent)
//    }


//    class AlarmReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            // 알림을 표시하는 코드 작성
//            showNotification(context)
//        }
//
//        private fun showNotification(context: Context) {
//            val notification: Notification =
//                NotificationCompat.Builder(context, GlobalApplication.CHANNEL_ID)
//                    .setContentTitle("알림 제목")
//                    .setContentText("알림 내용")
//                    .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .build()
//            val notificationManager = NotificationManagerCompat.from(context)
////            if (ActivityCompat.checkSelfPermission(
////                    this@MainActivity,
////                    Manifest.permission.POST_NOTIFICATIONS
////                ) != PackageManager.PERMISSION_GRANTED
////            ) {
////                // 권한이 없는 경우 권한 요청
////                // TODO: Deprecated 고려
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
////                    requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTI_PERMISSION_CODE)
////                }
////                return
////            }
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
////                requestPermissions(
////                    context,
////                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
////                    NOTI_PERMISSION_CODE
////                )
//                return
//            }
//            notificationManager.notify(1, notification)
//        }
//    }

}
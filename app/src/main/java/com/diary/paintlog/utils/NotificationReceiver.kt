package com.diary.paintlog.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.diary.paintlog.R


class NotificationReceiver : BroadcastReceiver() {
    private val tag = this.javaClass.simpleName

    companion object {
        const val notificationAction = "alarmNotification"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == notificationAction) {
            if (context != null) {
                NotifyManager.notify(
                    context,
                    context.getString(R.string.setting_notify_title),
                    context.getString(R.string.setting_notify_summary)
                )

                NotifyManager.setAlarm(context, true)
            } else {
                Log.e(tag, "context is null")
            }
        }
    }
}
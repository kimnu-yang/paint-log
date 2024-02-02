package com.diary.paintlog.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.diary.paintlog.R


class NotificationReceiver : BroadcastReceiver() {
    private val TAG = this.javaClass.simpleName

    companion object {
        val notificationAction = "alarmNotification"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == notificationAction) {
            Log.i(TAG, "Alarm Triggered! [${intent.action}]")
            if (context != null) {
                NotifyManager.notify(
                    context,
                    context.getString(R.string.setting_notify_title),
                    context.getString(R.string.setting_notify_summary)
                )

                NotifyManager.setAlarm(context, true)
            } else {
                Log.e(TAG, "context is null")
            }
        } else {
            Log.i(TAG, "Alarm Triggered?? [${intent?.action}]")
        }
    }
}
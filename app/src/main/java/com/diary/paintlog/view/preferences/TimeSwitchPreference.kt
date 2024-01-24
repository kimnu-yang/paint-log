package com.diary.paintlog.view.preferences

import android.app.TimePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreferenceCompat
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.R
import com.diary.paintlog.model.repository.AlarmRepository
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.runBlocking
import java.util.Calendar


class TimeSwitchPreference(context: Context, attrs: AttributeSet) : SwitchPreferenceCompat(context, attrs) {

    private var alarmRepo: AlarmRepository

    init {
        alarmRepo = AlarmRepository((context.applicationContext as GlobalApplication).dataStore)
    }


    override fun onAttached() {
        super.onAttached()

        // 설정한 알람 시간
        val time : AlarmRepository.Time = runBlocking { alarmRepo.getTime() }
        val preference: Preference? = super.getParent()?.findPreference("switch_alarm")

        preference?.summary = setTimeMsg(
            time.hour, time.minute
        )
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val switch: SwitchCompat = holder.findViewById(R.id.switch_alarm) as SwitchCompat

        val checked = runBlocking { alarmRepo.getChecked() }

        switch.isChecked = checked

        switch.setOnCheckedChangeListener { _, isChecked ->
            runBlocking { alarmRepo.saveChecked(isChecked) }
        }
    }

    override fun onClick() {
        val time : AlarmRepository.Time = runBlocking { alarmRepo.getTime() }

        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val preference: Preference? = super.getParent()?.findPreference("switch_alarm")

                preference?.summary = setTimeMsg(
                    selectedHour, selectedMinute
                )

                runBlocking { alarmRepo.save(selectedHour, selectedMinute) }
            },
            time.hour,
            time.minute,
            true
        )

        timePickerDialog.show()
    }

    private fun setTimeMsg(hour: Int, minute: Int): String {
        return hour.toString() + context.resources.getString(R.string.hour) + " " + minute.toString() + context.resources.getString(
            R.string.minute
        )
    }
}
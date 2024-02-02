package com.diary.paintlog.model.repository

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.diary.paintlog.GlobalApplication
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

class SettingsRepository() {

    private val dataStore = GlobalApplication.getInstance().dataStore

    companion object DataKeys {
        private val HOUR = intPreferencesKey("hour")
        private val MINUTE = intPreferencesKey("minute")
        private val CHECKED = booleanPreferencesKey("checked")
        private val LAST_SYNC_TIME = stringPreferencesKey("last_sync_time")
    }

    data class Time(val hour: Int, val minute: Int)

    suspend fun saveTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[HOUR] = hour
            preferences[MINUTE] = minute
        }
    }

    suspend fun getTime(): Time {
        return Time(dataStore.data.first()[HOUR] ?: -1, dataStore.data.first()[MINUTE] ?: -1)
    }

    suspend fun saveChecked(checked: Boolean) {
        dataStore.edit { preferences ->
            preferences[CHECKED] = checked
        }
    }

    suspend fun getChecked(): Boolean {
        return dataStore.data.first()[CHECKED] ?: false
    }

    suspend fun saveSyncTime(syncTime: LocalDateTime) {
        dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIME] = syncTime.toString()
        }
    }

    suspend fun getSyncTime(): String {
        return dataStore.data.first()[LAST_SYNC_TIME] ?: ""
    }

    suspend fun delSyncTime() {
        dataStore.edit {
            it.remove(LAST_SYNC_TIME)
        }
    }
}
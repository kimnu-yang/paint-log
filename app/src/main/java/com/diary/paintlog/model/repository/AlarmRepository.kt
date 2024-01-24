package com.diary.paintlog.model.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.preference.PreferenceDataStore
import com.diary.paintlog.model.ApiToken
import kotlinx.coroutines.flow.first

class AlarmRepository(private val dataStore: DataStore<Preferences>) {

    companion object DataKeys {
        private val HOUR = intPreferencesKey("hour")
        private val MINUTE = intPreferencesKey("minute")
        private val CHECKED = booleanPreferencesKey("checked")
    }

    data class Time(val hour: Int, val minute: Int)

    suspend fun save(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[HOUR] = hour
            preferences[MINUTE] = minute
        }
    }

    suspend fun getTime(): Time {
        return Time(dataStore.data.first()[HOUR] ?: 0, dataStore.data.first()[MINUTE] ?: 0)
    }

    suspend fun saveChecked(checked: Boolean) {
        dataStore.edit { preferences ->
            preferences[CHECKED] = checked
        }
    }

    suspend fun getChecked(): Boolean {
        return dataStore.data.first()[CHECKED] ?: false
    }

}
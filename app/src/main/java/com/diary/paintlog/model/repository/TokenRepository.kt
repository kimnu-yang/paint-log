package com.diary.paintlog.model.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.diary.paintlog.model.data.ApiToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TokenRepository(private val dataStore: DataStore<Preferences>) {
    private val KEY = "apiToken"

    private val Context.dataStore by preferencesDataStore(KEY)

    companion object DataKeys {
        private val accessTokenKey = stringPreferencesKey("access_token")
        private val accessTokenExpireAtKey = stringPreferencesKey("access_token_expire_at")
        private val refreshTokenKey = stringPreferencesKey("refresh_token")
        private val refreshTokenKeyExpireAtKey = stringPreferencesKey("refresh_token_expire_at")
    }

    suspend fun save(apiToken: ApiToken) {
        dataStore.edit { preferences ->
            preferences[accessTokenKey] = apiToken.accessToken
            preferences[accessTokenExpireAtKey] = apiToken.accessTokenExpireAt
            preferences[refreshTokenKey] = apiToken.refreshToken
            preferences[refreshTokenKeyExpireAtKey] = apiToken.refreshTokenExpireAt
        }
    }

    suspend fun save(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
        }
    }

    suspend fun getToken(): String {
        return dataStore.data.first()[accessTokenKey] ?: ""
    }

    suspend fun getTokenExpireAt(): String {
        return dataStore.data.first()[accessTokenExpireAtKey] ?: ""
    }

    suspend fun getRefreshToken(): String {
        return dataStore.data.first()[refreshTokenKey] ?: ""
    }

    suspend fun getRefreshTokenExpireAt(): String {
        return dataStore.data.first()[refreshTokenKeyExpireAtKey] ?: ""
    }
}
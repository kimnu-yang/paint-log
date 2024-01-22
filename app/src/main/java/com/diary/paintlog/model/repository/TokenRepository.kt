package com.diary.paintlog.model.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.diary.paintlog.model.ApiToken
import kotlinx.coroutines.flow.first

class TokenRepository(private val dataStore: DataStore<Preferences>) {

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
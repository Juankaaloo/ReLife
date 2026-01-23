package com.relife.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "relife_preferences")

class ReLifeDataStore(private val context: Context) {
    
    private object PreferencesKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_TOKEN = stringPreferencesKey("user_token")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val PUSH_NOTIFICATIONS = booleanPreferencesKey("push_notifications")
        val EMAIL_NOTIFICATIONS = booleanPreferencesKey("email_notifications")
    }
    
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
        }
    
    val userId: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }
    
    val darkMode: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.DARK_MODE] ?: false
        }
    
    val pushNotifications: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.PUSH_NOTIFICATIONS] ?: true
        }
    
    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }
    
    suspend fun setUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
        }
    }
    
    suspend fun setUserToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_TOKEN] = token
        }
    }
    
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }
    
    suspend fun setPushNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PUSH_NOTIFICATIONS] = enabled
        }
    }
    
    suspend fun setEmailNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EMAIL_NOTIFICATIONS] = enabled
        }
    }
    
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

package com.tensormind.feelio.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_GUEST = booleanPreferencesKey("is_guest")
    }

    val userData: Flow<UserData> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val userId = preferences[PreferencesKeys.USER_ID] ?: ""
            val name = preferences[PreferencesKeys.USER_NAME] ?: ""
            val isLoggedIn = preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
            val isGuest = preferences[PreferencesKeys.IS_GUEST] ?: false
            UserData(userId, name, isLoggedIn, isGuest)
        }

    suspend fun saveUser(userId: String, name: String, isGuest: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
            preferences[PreferencesKeys.IS_GUEST] = isGuest
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

data class UserData(
    val userId: String,
    val name: String,
    val isLoggedIn: Boolean,
    val isGuest: Boolean
)

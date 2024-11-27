package com.example.berongsok.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.berongsok.data.remote.response.Tps
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val THEME_KEY = booleanPreferencesKey("theme_setting")
    private val TPS_ID_KEY = stringPreferencesKey("tps_id")
    private val TPS_NAME_KEY = stringPreferencesKey("tps_name")
    private val TPS_EMAIL_KEY = stringPreferencesKey("tps_email")
    private val TPS_TOKEN_KEY = stringPreferencesKey("tps_token")
    private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }

    suspend fun saveUser(tps: Tps, b: Boolean) {
        dataStore.edit { preferences ->
            preferences[TPS_ID_KEY] = tps.tpsId
            preferences[TPS_NAME_KEY] = tps.name
            preferences[TPS_EMAIL_KEY] = tps.email
            preferences[TPS_TOKEN_KEY] = tps.token
            preferences[IS_LOGGED_IN_KEY] = b
        }
    }

    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.remove(TPS_ID_KEY)
            preferences.remove(TPS_NAME_KEY)
            preferences.remove(TPS_EMAIL_KEY)
            preferences.remove(TPS_TOKEN_KEY)
            preferences.remove(IS_LOGGED_IN_KEY)
        }
    }

    val tpsName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[TPS_NAME_KEY]
    }

    val tpsEmail: Flow<String?> = dataStore.data.map { preferences ->
        preferences[TPS_EMAIL_KEY]
    }

    val tpsId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[TPS_ID_KEY]
    }

    val tpsToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[TPS_TOKEN_KEY]
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] ?: false
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
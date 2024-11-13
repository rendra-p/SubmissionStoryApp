package com.example.mystoryapp.ui.welcome

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenDataStore private constructor(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: TokenDataStore? = null
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(context: Context): TokenDataStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenDataStore(context).also { INSTANCE = it }
            }
        }
    }

    val token: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY]
        }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}
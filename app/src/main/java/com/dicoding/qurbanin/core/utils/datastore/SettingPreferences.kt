package com.dicoding.qurbanin.core.utils.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.datastore : DataStore<Preferences> by  preferencesDataStore("settings")
class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>){
    private val TAG = SettingPreferences::class.java.simpleName

    private val AUTH_SESSION = booleanPreferencesKey("is_login")
    private val LOCATION = stringPreferencesKey("location")
    private val USER_NAME = stringPreferencesKey("user_name")

    fun isLogin() : Flow<Boolean> {
        return dataStore.data.map {
            it[AUTH_SESSION] ?: false
        }
    }

    suspend fun setLoginSession(isLogin : Boolean) {
        dataStore.edit {
            it[AUTH_SESSION] = isLogin
        }
    }

    fun getLocation() : Flow<String> {
        return dataStore.data.map {
            val data = it[LOCATION] ?: ""
            data
        }
    }

    suspend fun setLocation(location : String) {
        dataStore.edit {
            it[LOCATION] = location

            Log.i(TAG, "Location on Class SettingPreferences: $location")
        }
    }

    suspend fun deletePreference() {
        dataStore.edit {
            it.clear()
        }
    }

    fun getUsername() : Flow<String> {
        return dataStore.data.map {
            it[USER_NAME] ?: ""
        }
    }

    suspend fun setUserName(userName: String) {
        dataStore.edit {
            it[USER_NAME] = userName
        }
    }

    companion object {
        @Volatile
        private var INSTANCE : SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>) : SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
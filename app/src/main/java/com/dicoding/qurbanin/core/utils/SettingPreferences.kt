package com.dicoding.qurbanin.core.utils

import android.content.Context
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
    companion object {
        @Volatile
        private var INSTANCE : SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>) : SettingPreferences {
            return INSTANCE?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    private val AUTH_SESSION = booleanPreferencesKey("is_login")
    private val LOCATION = stringPreferencesKey("location")

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
        }
    }

    suspend fun deletePreference() {
        dataStore.edit {
            it.clear()
        }
    }
}
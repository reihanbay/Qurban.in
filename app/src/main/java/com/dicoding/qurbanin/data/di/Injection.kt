package com.dicoding.qurbanin.data.di

import android.content.Context
import com.dicoding.qurbanin.core.utils.api.ApiConfig
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.core.utils.datastore.datastore
import com.dicoding.qurbanin.data.Repository
import com.dicoding.qurbanin.data.api.LocationServices

object Injection {
    fun provideRepository(): Repository {
        val locationServices = ApiConfig.getApiService<LocationServices>()
        return Repository.getInstance(locationServices)
    }

    fun settingPreferences(context: Context): SettingPreferences =
        SettingPreferences.getInstance(context.datastore)
}
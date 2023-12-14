package com.dicoding.qurbanin.data.di

import android.content.Context
import com.dicoding.qurbanin.BuildConfig
import com.dicoding.qurbanin.core.utils.api.ApiConfig
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.core.utils.datastore.datastore
import com.dicoding.qurbanin.data.api.LocationServices
import com.dicoding.qurbanin.data.repository.QurbanRepository
import com.google.firebase.database.FirebaseDatabase

object Injection {
    fun provideRepository(): QurbanRepository {
        val locationServices = ApiConfig.getApiService<LocationServices>()
        val reference = FirebaseDatabase.getInstance(BuildConfig.DATABASE_URL).reference

        return QurbanRepository.getInstance(reference, locationServices)
    }

    fun settingPreferences(context: Context): SettingPreferences =
        SettingPreferences.getInstance(context.datastore)
}
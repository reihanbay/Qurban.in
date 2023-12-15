package com.dicoding.qurbanin.di

import android.content.Context
import com.dicoding.qurbanin.BuildConfig
import com.dicoding.qurbanin.core.utils.api.ApiConfig
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.core.utils.datastore.datastore
import com.dicoding.qurbanin.data.api.LocationServices
import com.dicoding.qurbanin.data.repository.AuthRepository
import com.dicoding.qurbanin.data.repository.LocationRepository
import com.dicoding.qurbanin.data.repository.QurbanRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object Injection {
    fun provideQurbanRepository() : QurbanRepository {
        val dbInstance = FirebaseDatabase.getInstance(BuildConfig.DATABASE_URL)
        return QurbanRepository.getInstance(dbInstance.reference)
    }

    fun provideLocationRepository() : LocationRepository {
        val locationServices = ApiConfig.getApiService<LocationServices>()

        return LocationRepository.getInstance(locationServices)
    }

    fun provideAuthRepository() : AuthRepository {
        val dbInstance = FirebaseDatabase.getInstance(BuildConfig.DATABASE_URL)
        val auth = FirebaseAuth.getInstance()
        return AuthRepository.getInstance(dbInstance.reference, auth)
    }

    fun settingPreferences(context: Context): SettingPreferences =
        SettingPreferences.getInstance(context.datastore)


}
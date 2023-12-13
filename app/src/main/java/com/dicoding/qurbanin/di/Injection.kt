package com.dicoding.qurbanin.di

import android.content.Context
import com.dicoding.qurbanin.BuildConfig
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.core.utils.datastore.datastore
import com.dicoding.qurbanin.data.repository.QurbanRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger

object Injection {
    fun provideQurbanRepository() : QurbanRepository {
        val dbInstance = FirebaseDatabase.getInstance(BuildConfig.DATABASE_URL)
        return QurbanRepository.getInstance(dbInstance.reference)
    }


}
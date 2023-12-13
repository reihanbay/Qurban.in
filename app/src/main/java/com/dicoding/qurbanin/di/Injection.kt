package com.dicoding.qurbanin.di

import com.dicoding.qurbanin.BuildConfig
import com.dicoding.qurbanin.data.repository.QurbanRepository
import com.google.firebase.database.FirebaseDatabase

object Injection {
    fun provideQurbanRepository() : QurbanRepository {
        val dbInstance = FirebaseDatabase.getInstance(BuildConfig.DATABASE_URL)
        return QurbanRepository.getInstance(dbInstance.reference)
    }


}
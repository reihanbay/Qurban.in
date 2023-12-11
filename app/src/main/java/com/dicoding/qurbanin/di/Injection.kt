package com.dicoding.qurbanin.di

import android.content.Context
import com.dicoding.qurbanin.BuildConfig
import com.dicoding.qurbanin.data.repository.QurbanRepository
import com.google.firebase.database.FirebaseDatabase

object Injection {
    fun provideQurbanRepository() : QurbanRepository {
        val reference = FirebaseDatabase.getInstance(BuildConfig.DATABASE_URL).reference
        return QurbanRepository.getInstance(reference)
    }
}
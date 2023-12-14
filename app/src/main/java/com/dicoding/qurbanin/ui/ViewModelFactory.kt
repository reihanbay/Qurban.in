package com.dicoding.qurbanin.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.data.Repository
import com.dicoding.qurbanin.data.di.Injection
import com.dicoding.qurbanin.ui.home.HomeViewModel

class ViewModelFactory private constructor(
    private val preferences: SettingPreferences,
    private val repository: Repository,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(preferences, repository) as T //non null assertion need attention
        }
        throw IllegalArgumentException("Unknown view model class " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                ViewModelFactory(
                    Injection.settingPreferences(context),
                    Injection.provideRepository(),
                )
            }.also { INSTANCE = it }
        }
    }
}
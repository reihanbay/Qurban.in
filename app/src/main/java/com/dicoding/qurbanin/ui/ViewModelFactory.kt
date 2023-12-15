package com.dicoding.qurbanin.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.di.Injection
import com.dicoding.qurbanin.ui.authentication.AuthenticationViewModel
import com.dicoding.qurbanin.ui.detail.QurbanViewModel
import com.dicoding.qurbanin.ui.search.SearchViewModel
import com.dicoding.qurbanin.ui.home.HomeViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(private val preferences: SettingPreferences,private val repoInject: Injection) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(AuthenticationViewModel::class.java) -> {
                return AuthenticationViewModel(preferences, repoInject.provideAuthRepository()) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                return SearchViewModel(repoInject.provideQurbanRepository()) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                return HomeViewModel(preferences, repoInject.provideLocationRepository()) as T
            }

            modelClass.isAssignableFrom(QurbanViewModel::class.java) -> {
                return QurbanViewModel(repoInject.provideQurbanRepository()) as T
            }
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
                    Injection
                )
            }.also { INSTANCE = it }
        }
    }
}
package com.dicoding.qurbanin.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.data.di.Injection
import com.dicoding.qurbanin.data.repository.QurbanRepository
import com.dicoding.qurbanin.ui.detail.QurbanViewModel
import com.dicoding.qurbanin.ui.search.SearchViewModel
import com.dicoding.qurbanin.ui.home.HomeViewModel

class ViewModelFactory private constructor(
    private val preferences: SettingPreferences,
    private val repository: QurbanRepository,
) : ViewModelProvider.NewInstanceFactory() {

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(private val repoInject: Injection) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                return SearchViewModel(repoInject.provideQurbanRepository()) as T
            }
            modelClass.isAssignableFrom(QurbanViewModel::class.java) -> {
                return QurbanViewModel(repoInject.provideQurbanRepository()) as T
            }
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(preferences, repository) as T
        } else if (modelClass.isAssignableFrom(QurbanViewModel::class.java)) {
            return QurbanViewModel(repository) as T
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
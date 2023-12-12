package com.dicoding.qurbanin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.qurbanin.di.Injection
import com.dicoding.qurbanin.ui.detail.QurbanViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(private val repoInject: Injection) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QurbanViewModel::class.java)) {
            return QurbanViewModel(repoInject.provideQurbanRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance : ViewModelFactory? = null
        fun getInstance(): ViewModelFactory =
            instance?: synchronized(this) {
                instance?: ViewModelFactory(Injection)
            }.also { instance = it }
    }
}
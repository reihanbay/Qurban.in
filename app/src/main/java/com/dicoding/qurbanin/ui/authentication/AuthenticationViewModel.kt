package com.dicoding.qurbanin.ui.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.UserResponse
import com.dicoding.qurbanin.data.repository.AuthRepository
import com.dicoding.qurbanin.data.repository.LocationRepository
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthenticationViewModel(private val preferences: SettingPreferences,
                              private val repository: AuthRepository,
) : ViewModel() {
    private val scope = viewModelScope

    private val _isLoginDone = MutableLiveData<Result<Boolean>>()
    val isLoginDone = _isLoginDone as LiveData<Result<Boolean>>

    private val _getDataUser = MutableLiveData<Result<UserResponse>>()
    val getDataUser = _getDataUser as LiveData<Result<UserResponse>>
    fun loginUser(email: String, password: String) = scope.launch {
        repository.loginUser(email, password).collect {
            when (it) {
                is Result.Success -> _isLoginDone.value = Result.Success(it.data)
                is Result.Error -> _isLoginDone.value = it
                is Result.Loading -> _isLoginDone.value = it
            }
        }
    }

    fun getDataUser() = scope.launch {
        repository.getDataUser().collect {
            when(it) {
                is Result.Success -> _getDataUser.value = Result.Success(it.data)
                is Result.Error -> _getDataUser.value = it
                is Result.Loading -> _getDataUser.value = it
            }
        }
    }

    fun setDataUserLocal(data: UserResponse) = scope.launch {
        val userName = data.Nama
        preferences.setLoginSession(true)
        preferences.setUserName(userName)
    }

    fun isLogin() : LiveData<Boolean> = preferences.isLogin().asLiveData()


    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}

//

package com.dicoding.qurbanin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.data.Repository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val preferences: SettingPreferences,
    private val repository: Repository,
) : ViewModel() {

    fun setUserLocation(location: String) {
        viewModelScope.launch {
            preferences.setLocation(location)
        }
    }

    fun getUserLocation() = preferences.getLocation()

    fun getProvinces() = repository.getProvinces()

    fun getRegencies(id: String) = repository.getRegencies(id)

    fun getDistricts(id: String) = repository.getDistricts(id)

    fun getVillages(id: String) = repository.getVillages(id)

}
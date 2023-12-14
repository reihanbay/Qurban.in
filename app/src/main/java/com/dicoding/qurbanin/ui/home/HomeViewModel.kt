package com.dicoding.qurbanin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.data.repository.LocationRepository
import com.dicoding.qurbanin.data.repository.QurbanRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val preferences: SettingPreferences,
    private val repository: LocationRepository,
) : ViewModel() {

    fun getUserName() = preferences.getUsername()

    fun getUserLocation() = preferences.getLocation()

    fun setUserLocation(location: String) {
        viewModelScope.launch {
            preferences.setLocation(location)
        }
    }

    fun getProvinces() = repository.getProvinces()

    fun getRegencies(id: String) = repository.getRegencies(id)

    fun getDistricts(id: String) = repository.getDistricts(id)

    fun getVillages(id: String) = repository.getVillages(id)

}
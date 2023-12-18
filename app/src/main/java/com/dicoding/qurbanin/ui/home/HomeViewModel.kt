package com.dicoding.qurbanin.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.DistrictResponse
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.data.model.ProvinceResponseItem
import com.dicoding.qurbanin.data.model.RegencyResponse
import com.dicoding.qurbanin.data.model.VillageResponse
import com.dicoding.qurbanin.data.repository.LocationRepository
import com.dicoding.qurbanin.data.repository.QurbanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val preferences: SettingPreferences,
    private val repository: LocationRepository,
    private val qurbanRepository: QurbanRepository
) : ViewModel() {

    private val _listEventData = MutableLiveData<Result<List<EventQurbanResponse>>>()
    val listEventData  : LiveData<Result<List<EventQurbanResponse>>> = _listEventData

    private val _listProvince = MutableLiveData<Result<List<ProvinceResponseItem>>>()
    val listProvince  : LiveData<Result<List<ProvinceResponseItem>>> = _listProvince

    private val _listRegencies = MutableLiveData<Result<List<RegencyResponse>>>()
    val listRegencies  : LiveData<Result<List<RegencyResponse>>> = _listRegencies

    private val _listDistrict = MutableLiveData<Result<List<DistrictResponse>>>()
    val listDistrict  : LiveData<Result<List<DistrictResponse>>> = _listDistrict

    private val _listVillage = MutableLiveData<Result<List<VillageResponse>>>()
    val listVillage  : LiveData<Result<List<VillageResponse>>> = _listVillage

    val location = MutableLiveData<String>()

    private val vmScope = viewModelScope

    fun getUserName() = preferences.getUsername().asLiveData()

    suspend fun getUserLocation() = preferences.getLocation().collect {
        vmScope.launch {
            location.value = it
        }

    }

    fun setUserLocation(location: String) {
        vmScope.launch {
            preferences.setLocation(location)
        }
    }

    fun getProvinces() {
        vmScope.launch {
            repository.getProvinces().collect {
                when(it) {
                    is Result.Success -> {
                        _listProvince.value = Result.Success(it.data)
                    }
                    else -> _listProvince.value = it
                }
            }
        }
    }

    fun getRegencies(id: String) {
        vmScope.launch {
            repository.getRegencies(id).collect {
                when(it) {
                    is Result.Success -> {
                        _listRegencies.value = Result.Success(it.data)
                    }
                    else -> _listRegencies.value = it
                }
            }
        }
    }

    fun getDistricts(id: String) {
        vmScope.launch {
            repository.getDistricts(id).collect {
                when(it) {
                    is Result.Success -> {
                        _listDistrict.value = Result.Success(it.data)
                    }
                    else -> _listDistrict.value = it
                }
            }
        }
    }

    fun getVillages(id: String) {
        vmScope.launch {
            repository.getVillages(id).collect {
                when(it) {
                    is Result.Success -> {
                        _listVillage.value = Result.Success(it.data)
                    }
                    else -> _listVillage.value = it
                }
            }
        }
    }

    val scope = viewModelScope
    fun getListEvent(location: String? = null) {
        scope.launch {
            qurbanRepository.getListEvent().collect {
                when(it) {
                    is Result.Success -> {
                        if (!location.isNullOrBlank()) getLocationList(location.toString(), it.data) else _listEventData.value = it
                    }
                    else -> _listEventData.value = it
                }
            }
        }
    }

    private fun getLocationList(location: String, listData: List<EventQurbanResponse>)  {
        scope.launch {
            val splitLocation = location.trim().split(",\\s+".toRegex())
            var searchUnionList = setOf<EventQurbanResponse>()
            withContext(Dispatchers.IO) {
                searchUnionList =searchUnionList union (listData.filter { it.data?.Lokasi!!.contains(splitLocation[3]) && it.data?.Lokasi!!.contains(splitLocation[2]) && it.data?.Lokasi!!.contains(splitLocation[1]) && it.data?.Lokasi!!.contains(splitLocation[0])})
                searchUnionList =searchUnionList union (listData.filter { it.data?.Lokasi!!.contains(splitLocation[3]) && it.data?.Lokasi!!.contains(splitLocation[2]) && it.data?.Lokasi!!.contains(splitLocation[1])})
                searchUnionList =searchUnionList union (listData.filter { it.data?.Lokasi!!.contains(splitLocation[3]) && it.data?.Lokasi!!.contains(splitLocation[2])})
                searchUnionList =searchUnionList union (listData.filter { it.data?.Lokasi!!.contains(splitLocation[3])})
            }
            searchUnionList = searchUnionList union listData.filter { it.data?.Lokasi!!.contains(splitLocation[3]) }
            _listEventData.value = Result.Success(searchUnionList.toList())
        }
    }

}
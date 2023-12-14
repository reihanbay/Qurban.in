package com.dicoding.qurbanin.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.api.LocationServices
import com.dicoding.qurbanin.data.model.DistrictResponse
import com.dicoding.qurbanin.data.model.ProvinceResponseItem
import com.dicoding.qurbanin.data.model.RegencyResponse
import com.dicoding.qurbanin.data.model.VillageResponse

class LocationRepository private constructor(private val locationService: LocationServices,
) {
    private val TAG = QurbanRepository::class.java.simpleName

    fun getProvinces(): LiveData<Result<List<ProvinceResponseItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = locationService.getProvince()
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e(TAG, "Error get provinces: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getRegencies(id: String): LiveData<Result<List<RegencyResponse>>> = liveData {
        emit(Result.Loading)
        try {
            val response = locationService.getRegency(id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e(TAG, "Error get regencies: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getDistricts(id: String): LiveData<Result<List<DistrictResponse>>> = liveData {
        emit(Result.Loading)
        try {
            val response = locationService.getDistricts(id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e(TAG, "Error get districts: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getVillages(id: String): LiveData<Result<List<VillageResponse>>> = liveData {
        emit(Result.Loading)
        try {
            val response = locationService.getVillages(id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e(TAG, "Error get provinces: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance : LocationRepository? = null
        fun getInstance(
            locationService: LocationServices,
        ) : LocationRepository =
            instance ?: synchronized(this) {
                instance ?: LocationRepository(
                    locationService,
                )
            }.also { instance = it }
    }
}
package com.dicoding.qurbanin.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.qurbanin.data.api.LocationServices
import com.dicoding.qurbanin.data.response.DistrictResponse
import com.dicoding.qurbanin.data.response.ProvinceResponseItem
import com.dicoding.qurbanin.data.response.RegencyResponse
import com.dicoding.qurbanin.data.response.VillageResponse

class Repository private constructor(
    private val locationService: LocationServices,
) {
    private val TAG = Repository::class.java.simpleName

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
        private var INSTANCE: Repository? = null

        fun getInstance(locationService: LocationServices): Repository {
            return INSTANCE ?: synchronized(this) {
                val instance = Repository(locationService)
                INSTANCE = instance
                instance
            }
        }
    }
}
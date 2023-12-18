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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class LocationRepository private constructor(private val locationService: LocationServices,
) {
    private val TAG = QurbanRepository::class.java.simpleName

    suspend fun getProvinces(): Flow<Result<List<ProvinceResponseItem>>> = callbackFlow {
        trySendBlocking(Result.Loading)
        val response = withContext(Dispatchers.IO) {
            try {
                Result.Success(locationService.getProvince())
            } catch (e: Exception) {
                Result.Error(e.message.toString())
            }
        }
        trySendBlocking(response)
        awaitClose { trySendBlocking(response) }
    }

    fun getRegencies(id: String): Flow<Result<List<RegencyResponse>>> = callbackFlow  {
        trySendBlocking(Result.Loading)
        val response = withContext(Dispatchers.IO) {
            try {
                Result.Success(locationService.getRegency(id))
            } catch (e: Exception) {
                Result.Error(e.message.toString())
            }
        }
        trySendBlocking(response)
        awaitClose { trySendBlocking(response) }
    }

    fun getDistricts(id: String): Flow<Result<List<DistrictResponse>>> = callbackFlow  {
        trySendBlocking(Result.Loading)
        val response = withContext(Dispatchers.IO) {
            try {
                Result.Success(locationService.getDistricts(id))
            } catch (e: Exception) {
                Result.Error(e.message.toString())
            }
        }
        trySendBlocking(response)
        awaitClose { trySendBlocking(response) }
    }

    fun getVillages(id: String): Flow<Result<List<VillageResponse>>> = callbackFlow  {
        trySendBlocking(Result.Loading)
        val response = withContext(Dispatchers.IO) {
            try {
                Result.Success(locationService.getVillages(id))
            } catch (e: Exception) {
                Result.Error(e.message.toString())
            }
        }
        trySendBlocking(response)
        awaitClose { trySendBlocking(response) }
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
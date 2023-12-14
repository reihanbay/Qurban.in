package com.dicoding.qurbanin.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.api.LocationServices
import com.dicoding.qurbanin.data.model.DataEventItem
import com.dicoding.qurbanin.data.model.DistrictResponse
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.data.model.ProvinceResponseItem
import com.dicoding.qurbanin.data.model.RegencyResponse
import com.dicoding.qurbanin.data.model.SoldByItem
import com.dicoding.qurbanin.data.model.StockDataItem
import com.dicoding.qurbanin.data.model.StockDataResponse
import com.dicoding.qurbanin.data.model.VillageResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QurbanRepository private constructor(
    private val dbReference: DatabaseReference,
    private val locationService: LocationServices,
) {
    private val TAG = QurbanRepository::class.java.simpleName
    
    fun getListEvent() : LiveData<Result<List<EventQurbanResponse>>> = liveData {
        val result = MutableLiveData<Result<List<EventQurbanResponse>>>()
        result.value = Result.Loading
        val dataList = mutableListOf<EventQurbanResponse>()
        CoroutineScope(Dispatchers.IO).launch {
            dbReference.child("Event").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach{
                        val data = it.getValue(DataEventItem::class.java)
                        dataList.add(EventQurbanResponse( it.key.toString(), data))
                    }
                    result.value = Result.Success(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    result.value = Result.Error(error.message)
                }
            })
        }
        emitSource(result)
    }

    fun getEventById(idEvent: String) : LiveData<Result<EventQurbanResponse>> = liveData{
        val result = MutableLiveData<Result<EventQurbanResponse>>()
        result.value = Result.Loading
        dbReference.child("Event").child(idEvent).get().addOnSuccessListener {
            val dataEvent = it.getValue(DataEventItem::class.java)
            val listStock = mutableListOf<StockDataResponse>()
            Log.d(TAG, "getEventData ${dbReference.child("Event").child(idEvent)} ")

            Log.d(TAG, "getEventData 1 $dataEvent ")
            //Get Data Stock
            it.child("Ketersediaan").children.forEach {stock ->
                val listSold = mutableListOf<SoldByItem>()
                Log.d(TAG, "getEventData 2 $stock ")

                //Get Data SoldBy
                stock.child("SoldBy").children.forEach { soldBy->
                    Log.d(TAG, "getEventData 3 $soldBy ")
                    //add Sold to ListSold
                    listSold.add(SoldByItem(soldBy.key.toString(),
                        soldBy.getValue(String::class.java).toString()
                    ))
                }

                //add Stock to ListStock
                listStock.add(StockDataResponse(stock.key.toString(), stock.getValue(StockDataItem::class.java),listSold))
            }
            result.value = Result.Success(EventQurbanResponse(it.key.toString(), dataEvent, listStock))

        }.addOnFailureListener {
            result.value = Result.Error(it.message?: "Failed Get Data")
        }
        Log.d(TAG, "getEventById: ${result.value} ")
        emitSource(result)
    }

    suspend fun getStockByIdEvent(idEvent: String) = withContext(Dispatchers.IO) {
        dbReference.child("Event").child(idEvent).child("Ketersediaan").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: $snapshot")
                Log.d(TAG, "onDataChange: ${snapshot.children}")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

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
        private var instance : QurbanRepository? = null
        fun getInstance(
            dbRef: DatabaseReference,
            locationService: LocationServices,
        ) : QurbanRepository =
            instance ?: synchronized(this) {
                instance ?: QurbanRepository(
                    dbRef,
                    locationService,
                )
            }.also { instance = it }
    }
}
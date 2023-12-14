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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QurbanRepository private constructor( private val dbReference: DatabaseReference,
) {
    suspend fun getListEvent() : Flow<Result<List<EventQurbanResponse>>> = callbackFlow {
            val dataList = mutableListOf<EventQurbanResponse>()
            trySendBlocking(Result.Loading)
            val listener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach{
                        val data = it.getValue(DataEventItem::class.java)
                        val listStock = mutableListOf<StockDataResponse>()

                        it.child("Ketersediaan").children.forEach {stock ->
                            val listSold = mutableListOf<SoldByItem>()
                            //Get Data SoldBy
                            stock.child("SoldBy").children.forEach { soldBy->
                                //add Sold to ListSold
                                listSold.add(SoldByItem(soldBy.key.toString(),
                                    soldBy.getValue(String::class.java).toString()
                                ))
                            }

                            //add Stock to ListStock
                            listStock.add(StockDataResponse(stock.key.toString(), stock.getValue(StockDataItem::class.java),listSold))
                        }
                        dataList.add(EventQurbanResponse( it.key.toString(), data, listStock))
                    }
                    trySendBlocking(Result.Success(dataList))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySendBlocking(Result.Error(error.message))
                }
            }
            dbReference.child("Event").addValueEventListener(listener)
       awaitClose {
           dbReference.child("Event").removeEventListener(listener)
       }
    }



    fun getEventById(idEvent: String) : LiveData<Result<EventQurbanResponse>> = liveData{
        val result = MutableLiveData<Result<EventQurbanResponse>>()
        result.value = Result.Loading
        CoroutineScope(Dispatchers.IO).launch {
            dbReference.child("Event").child(idEvent).get().addOnSuccessListener {
                val dataEvent = it.getValue(DataEventItem::class.java)
                val listStock = mutableListOf<StockDataResponse>()

                it.child("Ketersediaan").children.forEach {stock ->
                    val listSold = mutableListOf<SoldByItem>()
                    //Get Data SoldBy
                    stock.child("SoldBy").children.forEach { soldBy->
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
        }
        emitSource(result)
    }


    suspend fun getStockByIdEvent(idEvent: String) = withContext(Dispatchers.IO) {
        dbReference.child("Event").child(idEvent).child("Ketersediaan").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("TAG", "onDataChange: $snapshot")
                Log.d("TAG", "onDataChange: ${snapshot.children}")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    companion object {
        @Volatile
        private var instance : QurbanRepository? = null
        fun getInstance(
            dbRef: DatabaseReference,
        ) : QurbanRepository =
            instance ?: synchronized(this) {
                instance ?: QurbanRepository(
                    dbRef,
                )
            }.also { instance = it }
    }
}
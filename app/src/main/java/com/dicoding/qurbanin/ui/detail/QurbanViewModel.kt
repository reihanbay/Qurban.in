package com.dicoding.qurbanin.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.data.model.EventRegisteredResponse
import com.dicoding.qurbanin.data.model.EventRegisteredResponseItem
import com.dicoding.qurbanin.data.repository.QurbanRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class QurbanViewModel(private val repository: QurbanRepository) : ViewModel() {

    private val scope = viewModelScope

    private val _successRegistered = MutableLiveData<Result<Boolean>>()
    val successRegistered  : LiveData<Result<Boolean>> = _successRegistered

    private val _successPayment = MutableLiveData<Result<Boolean>>()
    val successPayment  : LiveData<Result<Boolean>> = _successPayment

    fun getEventById(idEvent: String) = repository.getEventById(idEvent)

    fun getEventRegisteredById(idEventRegistered: String) = repository.getEventRegisteredById(idEventRegistered)


    fun getStockById(idEvent: String, idStock:String) = repository.getStockById(idEvent, idStock)

    fun postPayment(idRegistered: String, data: EventRegisteredResponseItem) {
        _successPayment.value =  Result.Loading
        scope.launch {
           _successPayment.value = repository.postPayment(idRegistered,data)
        }
    }
    fun registerEvent(data: EventRegisteredResponseItem) {
        scope.launch {
            repository.registerEvent(data).collect {
                when (it) {
                    is Result.Success -> {
                        _successRegistered.value = it
                    }

                    else ->_successRegistered.value = it
                }
            }
        }
    }

}
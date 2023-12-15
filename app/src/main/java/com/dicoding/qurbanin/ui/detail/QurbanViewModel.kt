package com.dicoding.qurbanin.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.data.repository.QurbanRepository
import kotlinx.coroutines.launch

class QurbanViewModel(val repository: QurbanRepository) : ViewModel() {

    private val _eventData = MutableLiveData<Result<EventQurbanResponse>>()
    val eventData  : LiveData<Result<EventQurbanResponse>> = _eventData

    private val _listEventData = MutableLiveData<Result<List<EventQurbanResponse>>>()
    val listEventData  : LiveData<Result<List<EventQurbanResponse>>> = _listEventData
//    private val _stockData = MutableLiveData<Result<List<EventQurbanResponse>>>()
//    val stockData  : LiveData<Result<List<EventQurbanResponse>>> = _listEventData
    fun getListEvent() = repository.getListEvent()

    fun getEventById(idEvent: String) = repository.getEventById(idEvent)

    fun getStockByIdEvent(idEvent: String) {
        viewModelScope.launch {
            repository.getStockByIdEvent(idEvent)
        }
    }

    fun getEventByUserId(userId: String) = repository.getEventByUserId(userId)
    fun getEventByUserIdAndStatus(userId: String, status: String) = repository.getEventByUserIdAndStatus(userId, status)

}
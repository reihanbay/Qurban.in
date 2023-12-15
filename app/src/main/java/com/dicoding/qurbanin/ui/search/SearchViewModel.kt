package com.dicoding.qurbanin.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.data.repository.QurbanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(private val repository: QurbanRepository) : ViewModel() {

    private val _listEventData = MutableLiveData<Result<List<EventQurbanResponse>>>()
    val listEventData  : LiveData<Result<List<EventQurbanResponse>>> = _listEventData

    private val scope = viewModelScope
    fun getListEvent(location: String? = null) {
        scope.launch {
            repository.getListEvent().collect {
                when(it) {
                    is Result.Success -> {
                        if (!location.isNullOrBlank()) getLocationList(location.toString(), it.data) else _listEventData.value = it
                    }
                    else -> _listEventData.value = it
                }
            }
        }
    }

    fun getSearchListEvent(query: String?, list: List<EventQurbanResponse>) : List<EventQurbanResponse>{
        val data: MutableList<EventQurbanResponse> = mutableListOf()
        scope.launch {
            data.addAll(
                if (query.isNullOrBlank()) list
                else list.filter { it.data?.Name!!.contains(query, true) || it.data?.Lokasi!!.contains(query, true) }
            )
        }
        return data
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

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}